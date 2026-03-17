package io.github.a727786719.driver.license;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * License 核心功能封装类，使用 JNA 调用本地库。
 * <p>
 * 支持的功能：
 * <ul>
 *     <li>获取机器码</li>
 *     <li>获取最后的错误信息</li>
 *     <li>验证 License</li>
 *     <li>验证驱动 License JSON</li>
 * </ul>
 *
 * @see <a href="https://github.com/java-native-access/jna">JNA</a>
 */
public class LicenseCore implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(LicenseCore.class);

    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final int ERROR_BUFFER_SIZE = 1024;
    private static final int RESULT_JSON_SIZE = 8192;

    // Native library instance
    private final LicenseLibrary library;

    // Flag to track if the instance is closed
    private volatile boolean closed = false;

    /**
     * JNA 接口定义，映射本地库函数
     */
    public interface LicenseLibrary extends Library {
        /**
         * 获取机器码
         *
         * @param output     输出缓冲区
         * @param outputSize 缓冲区大小
         * @return 0 表示成功，非 0 表示失败
         */
        int get_machine_code(byte[] output, int outputSize);

        /**
         * 获取最后的错误信息
         *
         * @return 错误信息字符串指针
         */
        Pointer get_last_error();

        /**
         * 验证 License
         *
         * @param licensePath License 文件路径
         * @param driverID    驱动 ID
         * @param data        附加数据
         * @return 0 表示成功，非 0 表示失败
         */
        int VerifyLicense(String licensePath, String driverID, String data);

        /**
         * 验证驱动 License JSON
         *
         * @param payload       payload 字节数组
         * @param payloadLen    payload 长度
         * @param signature     signature 字节数组
         * @param signatureLen  signature 长度
         * @param payloadJSON   payload JSON 输出缓冲区
         * @param resultJSON    result JSON 输出缓冲区
         * @param resultSize    结果缓冲区大小
         * @param errorMsg      错误信息输出缓冲区
         * @param errorSize     错误缓冲区大小
         * @return 0 表示成功，非 0 表示失败
         */
        int verify_driver_license_json(
                byte[] payload, long payloadLen,
                byte[] signature, long signatureLen,
                byte[] payloadJSON, byte[] resultJSON, int resultSize,
                byte[] errorMsg, int errorSize
        );
    }

    /**
     * 创建 LicenseCore 实例，加载本地库并初始化。
     *
     * @throws UnsatisfiedLinkError 如果无法加载本地库
     */
    public LicenseCore() {
        // Load library from resources
        String libraryPath = NativeLibraryLoader.extractLibrary("license_core");
        this.library = Native.load(libraryPath, LicenseLibrary.class);
        log.info("LicenseCore initialized successfully with library: {}", libraryPath);
    }

    /**
     * 检查实例是否已关闭
     */
    private void checkNotClosed() {
        if (closed) {
            throw new IllegalStateException("LicenseCore instance has been closed");
        }
    }

    /**
     * 获取机器码
     *
     * @return 机器码字符串，如果获取失败则返回 null
     * @throws IllegalStateException 如果实例已关闭
     */
    public String getMachineCode() {
        checkNotClosed();

        try {
            byte[] output = new byte[DEFAULT_BUFFER_SIZE];
            int result = library.get_machine_code(output, DEFAULT_BUFFER_SIZE);

            if (result == 0) {
                return readNullTerminatedString(output);
            } else {
                String error = getLastError();
                log.error("Failed to get machine code: {}", error);
                return null;
            }
        } catch (Exception e) {
            log.error("Exception while calling get_machine_code", e);
            throw new LicenseException("Failed to get machine code", e);
        }
    }

    /**
     * 获取最后的错误信息
     *
     * @return 错误信息字符串，如果没有错误则返回 null
     * @throws IllegalStateException 如果实例已关闭
     */
    public String getLastError() {
        checkNotClosed();

        try {
            Pointer errorPtr = library.get_last_error();
            if (errorPtr == null) {
                return null;
            }
            return errorPtr.getString(0, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            log.error("Exception while calling get_last_error", e);
            return "Failed to get error message: " + e.getMessage();
        }
    }

    /**
     * 验证 License
     *
     * @param licensePath License 文件路径
     * @param driverID    驱动 ID
     * @param data        附加数据
     * @return 验证结果，0 表示成功，非 0 表示失败
     * @throws IllegalStateException 如果实例已关闭
     * @throws LicenseException     如果调用本地函数时发生异常
     */
    public int verifyLicense(String licensePath, String driverID, String data) {
        checkNotClosed();

        if (licensePath == null || driverID == null) {
            throw new IllegalArgumentException("licensePath and driverID cannot be null");
        }

        try {
            int result = library.VerifyLicense(licensePath, driverID, data);

            if (result != 0) {
                String error = getLastError();
                log.error("License verification failed (code={}): {}", result, error);
            }

            return result;
        } catch (Exception e) {
            log.error("Exception while calling VerifyLicense", e);
            throw new LicenseException("Failed to verify license", e);
        }
    }

    /**
     * 验证驱动 License JSON
     *
     * @param payload    License payload 字节数组
     * @param signature  License signature 字节数组
     * @return 验证结果对象，包含 payloadJSON、resultJSON 和错误信息
     * @throws IllegalStateException 如果实例已关闭
     * @throws LicenseException     如果调用本地函数时发生异常
     */
    public DriverLicenseResult verifyDriverLicenseJson(byte[] payload, byte[] signature) {
        checkNotClosed();

        if (payload == null || signature == null) {
            throw new IllegalArgumentException("payload and signature cannot be null");
        }

        try {
            byte[] payloadJsonBuf = new byte[RESULT_JSON_SIZE];
            byte[] resultJsonBuf = new byte[RESULT_JSON_SIZE];
            byte[] errorMsgBuf = new byte[ERROR_BUFFER_SIZE];

            int result = library.verify_driver_license_json(
                    payload, payload.length,
                    signature, signature.length,
                    payloadJsonBuf, resultJsonBuf, RESULT_JSON_SIZE,
                    errorMsgBuf, ERROR_BUFFER_SIZE
            );

            DriverLicenseResult licenseResult = new DriverLicenseResult();
            licenseResult.setCode(result);

            if (result == 0) {
                licenseResult.setPayloadJson(readNullTerminatedString(payloadJsonBuf));
                licenseResult.setResultJson(readNullTerminatedString(resultJsonBuf));
            } else {
                licenseResult.setErrorMsg(readNullTerminatedString(errorMsgBuf));
                log.error("Driver license verification failed (code={}): {}", result, licenseResult.getErrorMsg());
            }

            return licenseResult;
        } catch (Exception e) {
            log.error("Exception while calling verify_driver_license_json", e);
            throw new LicenseException("Failed to verify driver license JSON", e);
        }
    }

    /**
     * 从 null 终止的字节数组读取字符串
     */
    private String readNullTerminatedString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        int length = 0;
        while (length < bytes.length && bytes[length] != 0) {
            length++;
        }

        if (length == 0) {
            return "";
        }

        return new String(bytes, 0, length, StandardCharsets.UTF_8);
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            log.info("LicenseCore closed");
        }
    }
}

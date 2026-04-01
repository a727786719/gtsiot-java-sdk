package io.github.a727786719.driver.license;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

/**
 * 本地库加载器，用于从 JAR 资源中提取并加载本地库
 */
final class NativeLibraryLoader {

    private static final Logger log = LoggerFactory.getLogger(NativeLibraryLoader.class);

    private static final String TEMP_DIR_PREFIX = "license_core_";

    private NativeLibraryLoader() {
        // Utility class
    }

    /**
     * 获取库文件路径
     *
     * @param libraryName 库名称（不含平台前缀和后缀）
     * @return 库文件路径
     * @throws UnsatisfiedLinkError 如果无法提取库
     */
    static String getLibraryPath(String libraryName) {
        PlatformInfo platform = detectPlatform();
        return getResourcePath(libraryName, platform);
    }

    /**
     * 获取资源路径
     * <p>
     * 资源目录结构:
     * - windows_amd64/license_core_windows_amd64.dll
     * - linux_amd64/liblicense_core_linux_amd64.so
     * - darwin_amd64/liblicense_core_darwin_amd64.dylib
     */
    private static String getResourcePath(String libraryName, PlatformInfo platform) {
        String libraryFilename = getLibraryFileName(libraryName, platform);
        Path path = Paths.get(".").resolve(libraryFilename);
        if(path.toFile().exists()) {
            return path.toFile().getAbsolutePath();
        }

        path = Paths.get("../lib").resolve(libraryFilename);
        if(path.toFile().exists()) {
            return path.toFile().getAbsolutePath();
        }

        throw new LicenseException("未找到授权库文件");
    }

    /**
     * 获取库文件名
     * <p>
     * 文件命名格式: {libraryName}_{os}_{arch}.{ext}
     * 例如: license_core_windows_amd64.dll
     */
    private static String getLibraryFileName(String libraryName, PlatformInfo platform) {
        return switch (platform.os) {
            case "windows" -> libraryName + "_windows_" + platform.arch + ".dll";
            case "linux" -> "lib" + libraryName + "_linux_" + platform.arch + ".so";
            case "macos" -> "lib" + libraryName + "_darwin_" + platform.arch + ".dylib";
            default -> throw new UnsupportedOperationException("Unsupported OS: " + platform.os);
        };
    }

    /**
     * 检测当前平台信息
     */
    private static PlatformInfo detectPlatform() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String osArch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

        String os;
        if (osName.contains("win")) {
            os = "windows";
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            os = "macos";
        } else if (osName.contains("nux") || osName.contains("nix")) {
            os = "linux";
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + osName);
        }

        String arch;
        if (osArch.contains("amd64") || osArch.contains("x86_64")) {
            arch = "amd64";
        } else if (osArch.contains("aarch64") || osArch.contains("arm64")) {
            arch = "arm64";
        } else if(osArch.contains("loong64")) {
            arch = "loong64";
        } else {
            throw new UnsupportedOperationException("Unsupported architecture: " + osArch);
        }

        PlatformInfo info = new PlatformInfo(os, arch);
        log.debug("Detected platform: {}", info);
        return info;
    }

    /**
     * 平台信息
     */
    private record PlatformInfo(String os, String arch) {

        String getResourceDir() {
            return os + "_" + arch;
        }

        @Override
        public String toString() {
            return os + "-" + arch;
        }
    }
}

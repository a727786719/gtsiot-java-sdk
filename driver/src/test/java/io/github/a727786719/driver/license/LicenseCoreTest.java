package io.github.a727786719.driver.license;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LicenseCore 测试类
 */
class LicenseCoreTest {

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testGetMachineCode() {
        try (LicenseCore licenseCore = new LicenseCore()) {
            String machineCode = licenseCore.getMachineCode();
            // Machine code might be null if there's an error, but the call should not throw
            System.out.println("Machine code: " + machineCode);
        }
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testVerifyLicenseWithInvalidPath() {
        try(FileInputStream fis = new FileInputStream("F:\\GTSIOT_Workspace\\sdk-java\\sdk-driver-starter\\src\\test\\java\\io\\github\\gtsiot\\sdk\\driver\\license\\data.json")) {
            String data = new String(fis.readAllBytes(), StandardCharsets.UTF_8);
            try (LicenseCore licenseCore = new LicenseCore()) {
                int result = licenseCore.verifyLicense("D:\\GTSIOT\\airiot_42ef9c4ee4c2ee5ffa615c41eecad849a9b963_licenses", "kafka-driver", data);
                // Should fail with non-existent license file
                System.out.println("Error:" + result + ", " + licenseCore.getLastError());
                assertEquals(0, result);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testVerifyDriverLicenseJsonWithInvalidData() {
        try (LicenseCore licenseCore = new LicenseCore()) {
            byte[] payload = "invalid payload".getBytes();
            byte[] signature = "invalid signature".getBytes();

            DriverLicenseResult result = licenseCore.verifyDriverLicenseJson(payload, signature);
            // Should fail with invalid data
            assertFalse(result.isSuccess());
            assertNotNull(result.getErrorMsg());
        }
    }

    @Test
    void testLicenseCoreIsCloseable() {
        LicenseCore licenseCore = new LicenseCore();
        assertDoesNotThrow(licenseCore::close);

        // After closing, operations should throw IllegalStateException
        assertThrows(IllegalStateException.class, licenseCore::getMachineCode);
    }

    @Test
    void testDriverLicenseResult() {
        DriverLicenseResult result = new DriverLicenseResult();
        result.setCode(0);
        result.setPayloadJson("{\"test\": \"value\"}");
        result.setResultJson("{\"status\": \"ok\"}");

        assertTrue(result.isSuccess());
        assertEquals(0, result.getCode());
        assertEquals("{\"test\": \"value\"}", result.getPayloadJson());
        assertEquals("{\"status\": \"ok\"}", result.getResultJson());

        result.setCode(1);
        result.setErrorMsg("Test error");
        assertFalse(result.isSuccess());
        assertEquals("Test error", result.getErrorMsg());
    }
}

package com.smartschool.permit.tubespbo.repository.interfaces;

/**
 * Abstraksi utama repositori izin siswa yang menggabungkan kapabilitas
 * pembacaan (PermitReader) dan penulisan/mutasi (PermitWriter).
 * Menerapkan Dependency Inversion Principle (DIP) dan Interface Segregation Principle (ISP).
 */
public interface IPermitRepository extends PermitReader, PermitWriter {
}

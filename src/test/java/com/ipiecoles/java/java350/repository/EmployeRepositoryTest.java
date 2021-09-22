package com.ipiecoles.java.java350.repository;

import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

@DataJpaTest
class EmployeRepositoryTest {

    @Autowired
    private EmployeRepository employeRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        employeRepository.deleteAll();
    }

    @Test
    void findLastMatriculeBase() {
        //Given
        employeRepository.save(new Employe("Waddle", "Chris", "M00123", LocalDate.now().minusYears(2), Entreprise.SALAIRE_BASE, 1, 1.0));
        employeRepository.save(new Employe("Sauzet", "Franck", "T00123", LocalDate.now(), Entreprise.SALAIRE_BASE, 2, 0.5));
        employeRepository.save(new Employe("Dudu", "lala", "A00123", LocalDate.now().plusYears(2), Entreprise.SALAIRE_BASE, 3, 1.0));

        //When
        String matricule = employeRepository.findLastMatricule();

        //Then
        Assertions.assertEquals("00123", matricule);
    }

    @Test
    void findLastMatriculeOneEmploye() {
        //Given
        employeRepository.save(new Employe("Waddle", "Chris", "M00123", LocalDate.now().minusYears(2), Entreprise.SALAIRE_BASE, 1, 1.0));

        //When
        String matricule = employeRepository.findLastMatricule();

        //Then
        Assertions.assertEquals("00123", matricule);
    }

    @Test
    void findLastMatriculeNull() {
        //Given
        //Nothing to test null

        //When
        String matricule = employeRepository.findLastMatricule();

        //Then
        Assertions.assertNull(matricule);
    }


    @Test
    void avgPerformanceWhereMatriculeStartsWith_DB_Empty() {
        //Given

        //When
        Double avgPerf = employeRepository.avgPerformanceWhereMatriculeStartsWith("C");

        //Then
        Assertions.assertNull(avgPerf);
    }

    @Test
    void avgPerformanceWhereMatriculeStartsWith_OneEmployee() {
        //Given
        employeRepository.save(new Employe(null, null, "T10000", LocalDate.now().minusYears(2),
                Entreprise.SALAIRE_BASE, 1, 1.0));

        //When
        Double avgPerf = employeRepository.avgPerformanceWhereMatriculeStartsWith("T");

        //Then
        Assertions.assertEquals(1, avgPerf);
    }

    @Test
    void avgPerformanceWhereMatriculeStartsWith_SameType() {
        //Given

        employeRepository.save(new Employe(null, null, "C11111", LocalDate.now().minusYears(5),
                Entreprise.SALAIRE_BASE, 3, 1.0));
        employeRepository.save(new Employe(null, null, "C22222", LocalDate.now().minusYears(5),
                Entreprise.SALAIRE_BASE, 0, 1.0));

        //When
        Double avgPerf = employeRepository.avgPerformanceWhereMatriculeStartsWith("C");

        //Then
        Assertions.assertEquals(1.5, avgPerf);
    }


    @Test
    void avgPerformanceWhereMatriculeStartsWith_DifferentTypeOfEmployee() {
        //Given

        employeRepository.save(new Employe(null, null, "M10000", LocalDate.now().minusYears(5),
                Entreprise.SALAIRE_BASE, 3, 1.0));
        employeRepository.save(new Employe(null, null, "M20000", LocalDate.now().minusYears(5),
                Entreprise.SALAIRE_BASE, 1, 1.0));
        employeRepository.save(new Employe(null, null, "T10000", LocalDate.now().minusYears(5),
                Entreprise.SALAIRE_BASE, 3, 1.0));
        employeRepository.save(new Employe(null, null, "C10000", LocalDate.now().minusYears(5),
                Entreprise.SALAIRE_BASE, 1, 1.0));


        //When
        Double avgPerf = employeRepository.avgPerformanceWhereMatriculeStartsWith("M");

        //Then
        Assertions.assertEquals(2, avgPerf);
    }
}
package com.ipiecoles.java.java350.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

class EmployeTest {

    @Test
    void augmenterSalaire_valid() {
        Employe e = new Employe();
        e.setSalaire(1000.0d);

        e.augmenterSalaire(10);

        Assertions.assertEquals(1100.0d, e.getSalaire());
    }

    @Test
    void augmenterSalaire_SalaireNull() {
        Employe e = new Employe();
        e.setSalaire(null);

        //True si la méthode retourne une NullPointerException
        Assertions.assertThrows(NullPointerException.class, () -> e.augmenterSalaire(10));
    }

    @Test
    void augmenterSalaire_SalaireZero() {
        Employe e = new Employe();
        e.setSalaire(0.00d);

        e.augmenterSalaire(10);

        Assertions.assertEquals(0.0d, e.getSalaire());
    }

    @Test
    void augmenterSalaire_PourcentageNegatif() {
        Employe e = new Employe();
        e.setSalaire(1000.0d);

        //True si la méthode retourne une IllegalArgumentException
        Assertions.assertThrows(IllegalArgumentException.class, () -> e.augmenterSalaire(-50));
    }


    @ParameterizedTest
    @CsvSource({
            "2019, 1.0, 8",
            "2021, 0.5, 5",
            "2022, 1.0, 10",
            "2032, 1.0, 11",
    })
    void getNbRtt(Integer year, Double tempsPartiel, Integer nbRtt) {
        //Given
        Employe employe = new Employe("Doe", "John", "T12345", LocalDate.now().minusYears(3), Entreprise.SALAIRE_BASE, 1, tempsPartiel);
        LocalDate dateToday = LocalDate.of(year, 1, 1);

        //When
        Integer totalRtt = employe.getNbRtt(dateToday);

        //Then
        Assertions.assertEquals(totalRtt, nbRtt);

    }


    @Test
    void getNombreAnneeAncienneteNow() {
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now());

        //When
        Integer anciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(0, anciennete.intValue());
    }

    @Test
    void getNombreAnneeAnciennetePast() {
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().minusYears(5L));

        //When
        Integer anciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(5, anciennete.intValue());
    }

    @Test
    void getNombreAnneeAncienneteNull() {
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(null);

        //When
        Integer anciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(0, anciennete.intValue());
    }

    @Test
    void getNombreAnneeAncienneteFuture() {
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().plusYears(3L));

        //When
        Integer anciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(0, anciennete.intValue());
    }

    @ParameterizedTest
    @CsvSource({
            "2, 'T44888', 0, 1.0, 2300.0",
            "2, 'M12345', 1, 0.5, 900.0",
            "1, 'T44448', 3, 1.0, 1300.0",
            "2, 'T44488', 5, 1.0, 2800.0",
            "5, 'M12345', 5, 1.0, 2200.0",
            "0, 'T12345', 3, 1.0, 600.0",
            "1, 'M44488', 3, 1.0, 2000.0",
            "2, 'M44488', 5, 1.0, 2200.0"
    })
    void getPrimeAnnuelle(Integer performance, String matricule, Long nbYearsAnciennete, Double tempsPartiel, Double primeAnnuelle) {
        //Given
        Employe employe = new Employe("Nom", "Prénom", matricule, LocalDate.now().minusYears(nbYearsAnciennete), Entreprise.SALAIRE_BASE, performance, tempsPartiel);

        //When
        Double prime = employe.getPrimeAnnuelle();

        //Then
        Assertions.assertEquals(primeAnnuelle, prime);

    }
}
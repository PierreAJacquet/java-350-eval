package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EmployeServiceTest {

    @InjectMocks
    EmployeService employeService;

    @Mock
    EmployeRepository employeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this.getClass());
    }

    @Test
    void embaucheNewEmployeTechCapFullTime() throws EmployeException {
        //Given
        String prenom = "Chris";
        String nom = "Waddle";
        NiveauEtude etude = NiveauEtude.CAP;
        Poste poste = Poste.TECHNICIEN;
        Double temps = 1.0;
        Mockito.when(employeRepository.findByMatricule("T12345")).thenReturn(null);
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("12344");

        //When
        employeService.embaucheEmploye(nom, prenom, poste, etude, temps);
        //embaucheEmploye return void donc on passe par un ArgumentCaptor
        ArgumentCaptor<Employe> employe = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository, times(1)).save(employe.capture());

        //Then
        Assertions.assertEquals(prenom, employe.getValue().getPrenom());
        Assertions.assertEquals(nom, employe.getValue().getNom());
        Assertions.assertEquals(temps, employe.getValue().getTempsPartiel());
        Assertions.assertEquals("T12345", employe.getValue().getMatricule());
        //1521.22 * 1
        Assertions.assertEquals(1521.22, employe.getValue().getSalaire());
    }

    @Test
    void embaucheExistingEmployeTechCapFullTime() {
        //Given
        String prenom = "Chris";
        String nom = "Waddle";
        NiveauEtude etude = NiveauEtude.CAP;
        Poste poste = Poste.TECHNICIEN;
        Double temps = 1.0;
        Mockito.when(employeRepository.findByMatricule("T12345")).thenReturn(new Employe());
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("12344");

        //When
        EntityExistsException exception = Assertions.assertThrows(EntityExistsException.class, () -> employeService.embaucheEmploye(nom, prenom, poste, etude, temps));

        //Then
        Assertions.assertEquals("L'employé de matricule T12345 existe déjà en BDD", exception.getMessage());
    }

    @Test
    void embaucheNewEmployeTechCapFullTimeNoLastMatricule() throws EmployeException {
        //Given
        String prenom = "Chris";
        String nom = "Waddle";
        NiveauEtude etude = NiveauEtude.CAP;
        Poste poste = Poste.TECHNICIEN;
        Double temps = 1.0;
        Mockito.when(employeRepository.findByMatricule("T00001")).thenReturn(null);
        Mockito.when(employeRepository.findLastMatricule()).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, etude, temps);
        //embaucheEmploye return void donc on passe par un ArgumentCaptor
        ArgumentCaptor<Employe> employe = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository, times(1)).save(employe.capture());

        //Then
        Assertions.assertEquals("T00001", employe.getValue().getMatricule());
    }

    @Test
    void embaucheNewEmployeManagerMasterFullTime() throws EmployeException {
        //Given
        String prenom = "Chris";
        String nom = "Waddle";
        NiveauEtude etude = NiveauEtude.MASTER;
        Poste poste = Poste.MANAGER;
        Double temps = 0.5;
        Mockito.when(employeRepository.findByMatricule("M12345")).thenReturn(null);
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("12344");

        //When
        employeService.embaucheEmploye(nom, prenom, poste, etude, temps);
        //embaucheEmploye return void donc on passe par un ArgumentCaptor
        ArgumentCaptor<Employe> employe = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository, times(1)).save(employe.capture());

        //Then
        Assertions.assertEquals(prenom, employe.getValue().getPrenom());
        Assertions.assertEquals(nom, employe.getValue().getNom());
        Assertions.assertEquals(temps, employe.getValue().getTempsPartiel());
        Assertions.assertEquals("M12345", employe.getValue().getMatricule());
        //1521.22 * 1.4 * 0.5
        Assertions.assertEquals(1064.854, employe.getValue().getSalaire());
    }

    @Test
    void embaucheNewEmployeManagerMasterPartTimeLimiteMatricule() {
        //Given
        String prenom = "Chris";
        String nom = "Waddle";
        NiveauEtude etude = NiveauEtude.MASTER;
        Poste poste = Poste.MANAGER;
        Double temps = 0.5;
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("99999");

        //When
        EmployeException exception = Assertions.assertThrows(EmployeException.class, () -> employeService.embaucheEmploye(nom, prenom, poste, etude, temps));

        //Then
        Assertions.assertEquals("Limite des 100000 matricules atteinte !", exception.getMessage());

    }


    //////////////////////////

    @ParameterizedTest
    @CsvSource({
            "C24355, 42000, 25200, 4, 1",
            "C24355, 42000, 37800, 4, 3",
            "C24355, 42000, 42000, 4, 5",
            "C24355, 42000, 46200, 4, 6",
            "C24355, 42000, 54600, 4, 9",
    })
    void testCalculPerformanceCommercial(String matricule, Long objectifCa, Long caTraite, int performance, int expectedPerformance) throws EmployeException {
        //Given
        Employe employe = new Employe();
        employe.setMatricule(matricule);
        employe.setPerformance(performance);

        employeRepository.save(employe);

        Mockito.when(employeRepository.findByMatricule("C24355")).thenReturn(employe);
        Mockito.when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(1.5);

        //When
        employeService.calculPerformanceCommercial(employe.getMatricule(), caTraite, objectifCa);
        ArgumentCaptor<Employe> employeCaptured = ArgumentCaptor.forClass(Employe.class);

        //Then
        Mockito.verify(employeRepository, times(2)).save(employeCaptured.capture());
        Assertions.assertEquals(employeCaptured.getValue().getPerformance(), expectedPerformance);
    }


    @Test
    void calculPerformanceCommercialCaTraiteError() {
        //Given
        String matricule = "M12345";
        Long objectifCa = 10000L;
        Long caTraite = null;

        //When
        try {
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);
            Assertions.fail("Une exception devrait être apparu");
        } catch (EmployeException e) {
            //Then
            Assertions.assertEquals("Le chiffre d'affaire traité ne peut être négatif ou null !", e.getMessage());
        }

    }

    @Test
    void calculPerformanceCommercialObjectifCaError() {

        //Given
        String matricule = "M12345";
        Long caTraite = 10000L;
        Long objectifCa = null;


        //When
        try {
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);
            Assertions.fail("Une exception devrait être apparu");
        } catch (EmployeException e) {
            //Then
            Assertions.assertEquals("L'objectif de chiffre d'affaire ne peut être négatif ou null !", e.getMessage());
        }
    }

    @Test
    void calculPerformanceCommercialMatriculeError() {

        //Given
        Long caTraite = 10000L;
        Long objectifCa = 12000L;


        //When
        try {
            employeService.calculPerformanceCommercial(null, caTraite, objectifCa);
            Assertions.fail("Une exception devrait être apparu");
        } catch (EmployeException e) {
            //Then
            Assertions.assertEquals("Le matricule ne peut être null et doit commencer par un C !", e.getMessage());
        }
    }
}
package tn.dwelink.donationservice.service;

import tn.dwelink.donationservice.entity.RemiseSolidaire;

import java.util.List;

public interface IRemiseSolidaireService {
    RemiseSolidaire addRemiseSolidaire(RemiseSolidaire remiseSolidaire);
    RemiseSolidaire updateRemiseSolidaire(Long id, RemiseSolidaire remiseSolidaire);
    void deleteRemiseSolidaire(Long id);
    RemiseSolidaire retrieveRemiseSolidaire(Long id);
    List<RemiseSolidaire> retrieveAllRemisesSolidaires();
}

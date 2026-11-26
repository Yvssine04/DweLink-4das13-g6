package tn.dwelink.donationservice.service;

import tn.dwelink.donationservice.entity.ControleDon;

import java.util.List;

public interface IControleDonService {
    ControleDon addControleDon(ControleDon controleDon);
    ControleDon updateControleDon(Long id, ControleDon controleDon);
    void deleteControleDon(Long id);
    ControleDon retrieveControleDon(Long id);
    List<ControleDon> retrieveAllControleDons();
}

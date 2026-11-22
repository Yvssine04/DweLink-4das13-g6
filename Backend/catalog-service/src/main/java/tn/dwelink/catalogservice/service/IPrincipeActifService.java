package tn.dwelink.catalogservice.service;

import tn.dwelink.catalogservice.dto.PrincipeActifRequest;
import tn.dwelink.catalogservice.dto.PrincipeActifResponse;

import java.util.List;

public interface IPrincipeActifService  {

    PrincipeActifResponse creer(PrincipeActifRequest request);

    List<PrincipeActifResponse> rechercher(String q);
}



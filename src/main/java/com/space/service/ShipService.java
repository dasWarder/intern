package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.ShipType;
import javassist.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.space.model.Ship;

import java.io.FileNotFoundException;
import java.util.*;

public interface ShipService {

    //I HAVE STOPPED HERE!
    List<Ship> getShipsList(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed, Double minSpeed,
                            Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating,
                            ShipOrder order, Integer pageNumber, Integer pageSize);

    Ship createShip(Ship ship) throws FileNotFoundException;

    Ship getShipById(Long id) throws FileNotFoundException;

    Ship updateShip(Long id, Ship ship);

    void deleteShip(Long id);

    List<Ship> getFiltredShips(List <Ship> filtredList, ShipOrder order, Integer pageNumber, Integer pageSize);

}

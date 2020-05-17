package com.space.controller;

import com.space.exception.ExceptinBad;
import com.space.exception.FileNotFound;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import com.space.service.ShipServiceImp;
import com.sun.net.httpserver.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.awt.print.Pageable;
import java.util.*;
import java.io.FileNotFoundException;

@Controller
@ResponseBody
public class ShipController {

    @Autowired
    final ShipService shipService;

    public ShipController(ShipService shipService) {

        this.shipService = shipService;
    }




    @PostMapping(value = "/rest/ships")
    @ResponseBody
    public Ship createShip(@RequestBody Ship ship) throws FileNotFoundException {
        Ship createdShip = shipService.createShip(ship);

        if (createdShip != null) {
            return createdShip;
        }
        else throw new FileNotFoundException();
    }



    @GetMapping(value = "/rest/ships/{id}")
    public Ship getShip(@PathVariable("id") Long idOne) throws FileNotFoundException {
        if (idOne == null) {
            throw new ExceptinBad();
        }

        return shipService.getShipById(idOne);
    }


    @DeleteMapping(value = "/rest/ships/{id}")
    public void deleteShip(@PathVariable("id") Long id) {
        if (id == null) {
            throw new ExceptinBad();
        }

        shipService.deleteShip(id);
    }


    @GetMapping(value = "/rest/ships")
    public ResponseEntity<List<Ship>> getShipsList(@RequestParam(required = false) String name,
                                                   @RequestParam(required = false) String planet,
                                                   @RequestParam(required = false) ShipType shipType,
                                                   @RequestParam(required = false) Long after,
                                                   @RequestParam(required = false) Long before,
                                                   @RequestParam(required = false) Boolean isUsed,
                                                   @RequestParam(required = false) Double minSpeed,
                                                   @RequestParam(required = false) Double maxSpeed,
                                                   @RequestParam(required = false) Integer minCrewSize,
                                                   @RequestParam(required = false) Integer maxCrewSize,
                                                   @RequestParam(required = false) Double minRating,
                                                   @RequestParam(required = false) Double maxRating,
                                                   @RequestParam(required = false) ShipOrder order,
                                                   @RequestParam(required = false) Integer pageNumber,
                                                   @RequestParam(required = false) Integer pageSize) {
        List<Ship> shipsList = shipService.getShipsList(name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, order, pageNumber, pageSize);

        if (shipsList.isEmpty()) {
            return new ResponseEntity<>(shipsList, HttpStatus.NOT_FOUND);
        }

        List result = shipService.getFiltredShips(shipsList, order, pageNumber, pageSize);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping(value = "/rest/ships/count")
    public ResponseEntity<Integer> getCount(@RequestParam(required = false) String name,
                                            @RequestParam(required = false) String planet,
                                            @RequestParam(required = false) ShipType shipType,
                                            @RequestParam(required = false) Long after,
                                            @RequestParam(required = false) Long before,
                                            @RequestParam(required = false) Boolean isUsed,
                                            @RequestParam(required = false) Double minSpeed,
                                            @RequestParam(required = false) Double maxSpeed,
                                            @RequestParam(required = false) Integer minCrewSize,
                                            @RequestParam(required = false) Integer maxCrewSize,
                                            @RequestParam(required = false) Double minRating,
                                            @RequestParam(required = false) Double maxRating,
                                            @RequestParam(required = false) ShipOrder order,
                                            @RequestParam(required = false) Integer pageNumber,
                                            @RequestParam(required = false) Integer pageSize) {

        Integer size = shipService.getShipsList(name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, order, pageNumber, pageSize).size();
        return new ResponseEntity<>(size, HttpStatus.OK);
    }

    @PostMapping(value = "/rest/ships/{id}")
    @ResponseBody
    public ResponseEntity<Ship> updateShip(@PathVariable Long id, @RequestBody Ship ship) {
        if (id == 0 || id == null) {
            throw new ExceptinBad();
        }
        Ship updated = this.shipService.updateShip(id,ship);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }



}

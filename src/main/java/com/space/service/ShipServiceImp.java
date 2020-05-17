package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exception.ExceptinBad;
import com.space.exception.FileNotFound;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.io.FileNotFoundException;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShipServiceImp implements ShipService {

    @Autowired
    final ShipRepository shipRepository;


    public ShipServiceImp(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    //I HAVE STOPPED HERE! PAGEABLE FUNCTION
    @Override
    public List<Ship> getShipsList(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed, Double minSpeed,
                                   Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating,
                                   ShipOrder order, Integer pageNumber, Integer pageSize) {

        List<Ship> filteredShips = shipRepository.findAll();
        if (name != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getName().contains(name))
                    .collect(Collectors.toList());
        }
        if (planet != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getPlanet().contains(planet))
                    .collect(Collectors.toList());
        }
        if (shipType != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getShipType().equals(shipType))
                    .collect(Collectors.toList());
        }
        if (after != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getProdDate().after(new Date(after)))
                    .collect(Collectors.toList());
        }
        if (before != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getProdDate().before(new Date(before)))
                    .collect(Collectors.toList());
        }
        if (isUsed != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getUsed().equals(isUsed))
                    .collect(Collectors.toList());
        }
        if (minSpeed != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getSpeed() >= minSpeed)
                    .collect(Collectors.toList());
        }
        if (maxSpeed != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getSpeed() <= maxSpeed)
                    .collect(Collectors.toList());
        }
        if (minCrewSize != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getCrewSize() >= minCrewSize)
                    .collect(Collectors.toList());
        }
        if (maxCrewSize != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getCrewSize() <= maxCrewSize)
                    .collect(Collectors.toList());
        }
        if (minRating != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getRating() >= minRating)
                    .collect(Collectors.toList());
        }
        if (maxRating != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getRating() <= maxRating)
                    .collect(Collectors.toList());
        }


        return filteredShips;
    }

    @Override
    public List<Ship> getFiltredShips(List<Ship> filtredList, ShipOrder order, Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) {
            pageNumber = 0;
        }
        if (pageSize == null) {
            pageSize = 3;
        }

        if (!filtredList.isEmpty()) {
            filtredList.sort(getComparator(order));
            return filtredList.stream().skip(pageNumber*pageSize).limit(pageSize).collect(Collectors.toList());
        }

        return null;
    }

    private Comparator<Ship> getComparator(ShipOrder order) {
        if (order == null) {
            return new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            };
        }
        switch (order.getFieldName()) {
            case "id": return new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            };

            case "speed": return new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    return o1.getSpeed().compareTo(o2.getSpeed());
                }
            };

            case "prodDate": return new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    return o1.getProdDate().compareTo(o2.getProdDate());
                }
            };

            case "rating": return new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    return o1.getRating().compareTo(o2.getRating());
                }
            };
        }

        return null;
    }


    @Override
    public Ship createShip(Ship ship)  {
        if (ship != null) {
            if (ship.getUsed() == null) {
                ship.setUsed(false);
            }
            if (ship.getName() == null ||
                    ship.getName().isEmpty() ||
                    ship.getName().length() > 50 ||
                    ship.getPlanet() == null ||
                    ship.getPlanet().isEmpty() ||
                    ship.getPlanet().length() > 50 ||
                    ship.getShipType() == null ||
                    ship.getProdDate() == null ||
                    ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() < 2800 ||
                    ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() > 3019 ||
                    ship.getSpeed() == null ||
                    ship.getSpeed() < 0.01d ||
                    ship.getSpeed() > 0.99d ||
                    ship.getCrewSize() == null ||
                    ship.getCrewSize() < 1 ||
                    ship.getCrewSize() > 9999) {
                throw new ExceptinBad();
            }


            ship.setSpeed((double) Math.round(ship.getSpeed() * 100) / 100);
            ship.setRating(computationRating(ship));
            return shipRepository.save(ship);
        } else throw new ExceptinBad();
    }

    @Override
    public Ship getShipById(Long id) {
        if (id > 0) {
            if (shipRepository.existsById(id)) {

                return shipRepository.findById(id).orElse(null);
            }
            else throw new FileNotFound();
        }
        else throw new ExceptinBad();
    }

    //NULL POINTER EXCEPTION FOR 2 LAST TESTS
    @Override
    @Transactional
    public Ship updateShip(Long id, Ship newShip) {
        Ship shipUpdate = getShipById(id);

        if (newShip.getName() == null && newShip.getPlanet() == null &&
                newShip.getShipType() == null && newShip.getProdDate() == null &&
                newShip.getUsed() == null && newShip.getSpeed() == null &&
                newShip.getCrewSize() == null) {
            return shipRepository.save(getShipById(id));
        }

        if (newShip == null || shipUpdate == null) {
            throw new ExceptinBad();
        }
        if (newShip.getName() != null) {
            if (newShip.getName().length() > 50 ||
                newShip.getName().isEmpty()) {
                    throw new ExceptinBad();
                }
                shipUpdate.setName(newShip.getName());
        }
        if (newShip.getPlanet() != null) {
            if (newShip.getPlanet().length() > 50 ||
                    newShip.getPlanet().isEmpty()) {
                throw new ExceptinBad();
            }
            shipUpdate.setPlanet(newShip.getPlanet());
        }
        if (newShip.getShipType() != null) {
            shipUpdate.setShipType(newShip.getShipType());
        }
        if (newShip.getProdDate() != null) {
            if (newShip.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() < 2800 ||
                    newShip.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() > 3019) {
                throw new ExceptinBad();
            }
            shipUpdate.setProdDate(newShip.getProdDate());
        }
        if (newShip.getUsed() != null) {
            shipUpdate.setUsed(newShip.getUsed());
        }
        if (newShip.getSpeed() != null) {
            if (newShip.getSpeed() < 0.01d ||
                    newShip.getSpeed() > 0.99d) {
                throw new ExceptinBad();
            }
            shipUpdate.setSpeed(newShip.getSpeed());
        }
        if (newShip.getCrewSize() != null) {
            if (newShip.getCrewSize() < 1 ||
                    newShip.getCrewSize() > 9999) {
                throw new ExceptinBad();
            }
            shipUpdate.setCrewSize(newShip.getCrewSize());
        }

        shipUpdate.setRating(computationRating(shipUpdate));
        return shipRepository.save(shipUpdate);
    }

    @Override
    public void deleteShip(Long id) {
        if (id > 0) {
            if (shipRepository.existsById(id)) {
                shipRepository.deleteById(id);
            } else throw new FileNotFound();
        }
        else throw new ExceptinBad();
    }

    private Double computationRating(Ship ship) {
        double speed = ship.getSpeed();
        double coefficientUsed = ship.getUsed() ? 0.5d : 1.0d;
        int currentYear = 3019;
        int productionDate = ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
        double rating = (80 * speed * coefficientUsed) / (double) (currentYear - productionDate + 1);
        return (double) Math.round(rating * 100) / 100;
    }


}

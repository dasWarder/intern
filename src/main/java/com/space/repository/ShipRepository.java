package com.space.repository;

import java.util.*;
import com.space.model.Ship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ShipRepository extends JpaRepository<Ship, Long> {

}

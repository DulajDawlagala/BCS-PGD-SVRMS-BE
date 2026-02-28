////VehicleController.java
// package com.svrmslk.company.vehicle.presentation;
//
//import com.svrmslk.company.shared.api.ApiResponse;
//import com.svrmslk.company.vehicle.application.VehicleService;
//import com.svrmslk.company.vehicle.application.command.AddVehicleCommand;
//import com.svrmslk.company.vehicle.application.command.UpdateVehicleCommand;
//import com.svrmslk.company.vehicle.domain.Vehicle;
//import com.svrmslk.company.vehicle.presentation.dto.AddVehicleRequest;
//import com.svrmslk.company.vehicle.presentation.dto.UpdateVehicleRequest;
//import com.svrmslk.company.vehicle.presentation.dto.VehicleResponse;
//import com.svrmslk.company.vehicle.presentation.mapper.VehicleDtoMapper;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/v1/vehicles")
//@RequiredArgsConstructor
//@Tag(name = "Vehicle Management")
//public class VehicleController {
//
//    private final VehicleService vehicleService;
//    private final VehicleDtoMapper mapper;
//
//    @PostMapping
//    @Operation(summary = "Add vehicle to company fleet")
//    public ResponseEntity<ApiResponse<VehicleResponse>> addVehicle(@RequestBody AddVehicleRequest request) {
//        AddVehicleCommand command = mapper.toAddCommand(request);
//        Vehicle vehicle = vehicleService.addVehicle(command);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.success(mapper.toResponse(vehicle), "Vehicle added successfully"));
//    }
//
//    @PutMapping("/{vehicleId}")
//    @Operation(summary = "Update vehicle")
//    public ResponseEntity<ApiResponse<VehicleResponse>> updateVehicle(
//            @PathVariable UUID vehicleId,
//            @RequestBody UpdateVehicleRequest request) {
//
//        UpdateVehicleCommand command = mapper.toUpdateCommand(request);
//        Vehicle vehicle = vehicleService.updateVehicle(vehicleId, command);
//        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(vehicle)));
//    }
//
//    @GetMapping("/{vehicleId}")
//    @Operation(summary = "Get vehicle details")
//    public ResponseEntity<ApiResponse<VehicleResponse>> getVehicle(@PathVariable UUID vehicleId) {
//        Vehicle vehicle = vehicleService.getVehicle(vehicleId);
//        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(vehicle)));
//    }
//
//    @GetMapping("/company/{companyId}")
//    @Operation(summary = "Get all vehicles for a company")
//    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getVehiclesByCompany(@PathVariable UUID companyId) {
//        List<Vehicle> vehicles = vehicleService.getVehiclesByCompany(companyId);
//        List<VehicleResponse> responses = vehicles.stream()
//                .map(mapper::toResponse)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(ApiResponse.success(responses));
//    }
//
//    @DeleteMapping("/{vehicleId}")
//    @Operation(summary = "Delete vehicle")
//    public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable UUID vehicleId) {
//        vehicleService.deleteVehicle(vehicleId);
//        return ResponseEntity.ok(ApiResponse.success(null, "Vehicle deleted successfully"));
//    }
//}

package com.svrmslk.company.vehicle.presentation;

import com.svrmslk.company.shared.api.ApiResponse;
import com.svrmslk.company.vehicle.application.VehicleService;
import com.svrmslk.company.vehicle.application.command.AddVehicleCommand;
import com.svrmslk.company.vehicle.application.command.UpdateVehicleCommand;
import com.svrmslk.company.vehicle.domain.Vehicle;
import com.svrmslk.company.vehicle.domain.VehicleStatus;
import com.svrmslk.company.vehicle.presentation.dto.AddVehicleRequest;
import com.svrmslk.company.vehicle.presentation.dto.UpdateVehicleRequest;
import com.svrmslk.company.vehicle.presentation.dto.VehicleResponse;
import com.svrmslk.company.vehicle.presentation.mapper.VehicleDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicle Management")
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleDtoMapper mapper;

    @PostMapping
    @Operation(summary = "Add vehicle to company fleet")
    public ResponseEntity<ApiResponse<VehicleResponse>> addVehicle(@RequestBody AddVehicleRequest request) {
        AddVehicleCommand command = mapper.toAddCommand(request);
        Vehicle vehicle = vehicleService.addVehicle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(mapper.toResponse(vehicle), "Vehicle added successfully"));
    }

    @PutMapping("/{vehicleId}")
    @Operation(summary = "Update vehicle")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateVehicle(
            @PathVariable UUID vehicleId,
            @RequestBody UpdateVehicleRequest request) {

        UpdateVehicleCommand command = mapper.toUpdateCommand(request);
        Vehicle vehicle = vehicleService.updateVehicle(vehicleId, command);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(vehicle)));
    }

    @GetMapping("/{vehicleId}")
    @Operation(summary = "Get vehicle details")
    public ResponseEntity<ApiResponse<VehicleResponse>> getVehicle(@PathVariable UUID vehicleId) {
        Vehicle vehicle = vehicleService.getVehicle(vehicleId);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(vehicle)));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get all vehicles for a company")
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getVehiclesByCompany(@PathVariable UUID companyId) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByCompany(companyId);
        List<VehicleResponse> responses = vehicles.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // ✅ NEW: Get all AVAILABLE vehicles across all companies (public browsing)
    @GetMapping("/available")
    @Operation(summary = "Get all available vehicles for rental (public)")
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getAvailableVehicles(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Integer maxYear) {

        List<Vehicle> vehicles = vehicleService.getAvailableVehicles(make, model, minYear, maxYear);
        List<VehicleResponse> responses = vehicles.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // ✅ NEW: Change vehicle status (MAINTENANCE, AVAILABLE)
    @PatchMapping("/{vehicleId}/status")
    @Operation(summary = "Update vehicle status")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateVehicleStatus(
            @PathVariable UUID vehicleId,
            @RequestParam VehicleStatus status) {

        Vehicle vehicle = vehicleService.updateVehicleStatus(vehicleId, status);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(vehicle), "Vehicle status updated"));
    }

    @DeleteMapping("/{vehicleId}")
    @Operation(summary = "Delete vehicle")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable UUID vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
        return ResponseEntity.ok(ApiResponse.success(null, "Vehicle deleted successfully"));
    }
}
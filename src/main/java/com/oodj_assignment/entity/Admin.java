package com.oodj_assignment.entity;

import com.oodj_assignment.Logoutable;
import com.oodj_assignment.UI.menu.AdminMenu;
import com.oodj_assignment.UI.CompanyReport;
import com.oodj_assignment.UI.menu.MainMenu;
import com.oodj_assignment.entity.Booking.BookingStatus;
import com.oodj_assignment.helper.ArrayUtils;
import com.oodj_assignment.helper.RecordReader;
import com.oodj_assignment.helper.RecordUpdater;
import com.oodj_assignment.helper.RecordWriter;
import com.oodj_assignment.helper.UI.JTableInserter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTable;

public class Admin extends User implements Logoutable {

    public Admin(String userID) {
        super(userID);
    }
    
    @Override
    public void viewMenu() {
        new AdminMenu(this).setVisible(true);
    }
    
    public void addCar(Car newCar) {
        RecordWriter.write(new String[] {
            newCar.getPlateNum(), 
            newCar.getModel(), 
            newCar.getColour(), 
            String.valueOf(newCar.getPricePerDay()), 
            newCar.getStatus()
        }, "car.txt");
    }

    public void editCar(Car selectedCar) {
        RecordUpdater.update(new String[] {
            selectedCar.getPlateNum(), 
            selectedCar.getModel(), 
            selectedCar.getColour(), 
            String.valueOf(selectedCar.getPricePerDay()), 
            selectedCar.getStatus()
        }, "car.txt");
    }
    
    public void deleteCar(Car selectedCar) {
        String[][] cars = RecordReader.readFile("car.txt");
        for (String[] car : cars) {        
           if (car[0].equals(selectedCar.getPlateNum())) {
                cars = ArrayUtils.removeElement(cars, car);
           }
        } 
        RecordWriter.write(cars, "car.txt", true);
    }
    
    @Override
    public void searchCar(String keyword) {
        keyword = keyword.trim().toUpperCase();
        String[] carFields = {"Plate Number", "Model", "Colour", "Price/Day", "Status"};
        String[][] carsInfo = RecordReader.readFile("car.txt");
        if (!"E.G. AXIA/(PLATE NUMBER)".equals(keyword)) {
            for (String[] carInfo : carsInfo) {
                String plateNum = carInfo[0].toUpperCase();
                String model = carInfo[1].toUpperCase();
                if (!model.contains(keyword) && !plateNum.contains(keyword)) {
                    carsInfo = ArrayUtils.removeElement(carsInfo, carInfo); 
                }
            }
        }
        JTable adminTable = AdminMenu.getTable();
        JTableInserter.insert(carFields, carsInfo, adminTable);
    }
    
    public void viewBookingRequest() {
        String[] field = {"Booking ID", "Plate number", "Pick-up date", "Return date", 
            "Duration(Day)", "Price/Day", "Total price", "Status"};
        String[][] bookingRecords = RecordReader.readFile("booking.txt");
        List<String[]> bookingRequests = new ArrayList();
        if (bookingRecords.length > 0) {
            for (String[] bookingRecord : bookingRecords) {
                if (bookingRecord[5].equals(Booking.BookingStatus.PENDING.name())) {
                    Booking booking = new Booking(bookingRecord[0]);
                    bookingRequests.add(new String[] {
                        booking.getBookingID(),
                        booking.getSelectedCar().getPlateNum(),
                        booking.getPickupDate().toString(),
                        booking.getReturnDate().toString(),
                        String.valueOf(booking.getRentDuration()),
                        String.valueOf(booking.getSelectedCar().getPricePerDay()),
                        String.valueOf(booking.getTotalPrice()),
                        booking.getStatus().name()
                    });
                }
            }    
        }
        JTable adminTable = AdminMenu.getTable();
        JTableInserter.insert(field, bookingRequests.toArray(new String[0][]), adminTable); 
    }
   
    public void updateBookingRequest(Booking booking) {
        String bookingID = booking.getBookingID();
        String customerID = booking.getMember().getUserID();
        String plateNum = booking.getSelectedCar().getPlateNum();
        String pickupDate = booking.getPickupDate().toString();
        String returnDate = booking.getReturnDate().toString();
        String bookingStatus = booking.getStatus().name();
        String[] bookingRecord = new String[] {bookingID, customerID, plateNum, pickupDate, 
            returnDate, bookingStatus};
        RecordUpdater.update(bookingRecord, "booking.txt");
    }
    
    public void viewRecord(String type) {
        String[] fields = switch (type) {
            case "payment" -> new String[] {
                "Payment ID", "Booking ID", "Payment date", "Total paid (RM)"
            };
            case "booking" -> new String[] {
                "Booking ID", "Customer ID", "Plate number", "Pick-up date", "Return date", "Status"
            };
            case "customer" -> new String[] {
                "Customer ID", "Email", "Username", "Phone number"
            };
            case "car" -> new String[] {
                "Plate number", "Model", "Colour", "Price/Day", "Status"
            };
            default -> null;
        };
        type = "customer".equals(type) ? "user" : type;  
        String[][] records = RecordReader.readFile(type + ".txt");
        
        // Remove admin from user records to form customer records.
        if ("user".equals(type)) {
            for (String[] record : records) {
                if ("adm".equals(record[0].substring(0, 3))) {
                    records = ArrayUtils.removeElement(records, record);
                }
            }
        }
        if ("booking".equals(type)) {
            for (String[] record : records) {
                String status = record[5];
                if (BookingStatus.PENDING.name().equals(status)) {
                    records = ArrayUtils.removeElement(records, record);
                }
            }
        }
        if (null != fields && null != records) {
            JTable adminTable = AdminMenu.getTable();
            JTableInserter.insert(fields, records, adminTable);
        }    
    }
    
    public void viewCompanyReport() {
        new CompanyReport(this).setVisible(true);
    }
    
    @Override
    public void logout(JFrame adminMenu) {
        adminMenu.dispose();
        new MainMenu().setVisible(true);
    }
    
}

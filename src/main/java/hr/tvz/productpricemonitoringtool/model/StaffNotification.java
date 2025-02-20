package hr.tvz.productpricemonitoringtool.model;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.SerializationException;
import hr.tvz.productpricemonitoringtool.model.dbo.UserCompanyDBO;
import hr.tvz.productpricemonitoringtool.repository.UserCompanyRepository;
import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public non-sealed class StaffNotification extends Notification implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(StaffNotification.class);
    protected static List<UserCompanyDBO> deserializedUserCompanyDBOList = new ArrayList<>();
    protected static List<UserCompanyDBO> newUserCompanyDBORecords = new ArrayList<>();

    private final transient UserCompanyRepository userCompanyRepository = new UserCompanyRepository();

    public StaffNotification() {
        log.info("Staff Notification");
    }

    public static List<UserCompanyDBO> getNewUserCompanyDBORecords() {
        return newUserCompanyDBORecords;
    }

    public static void setNewUserCompanyDBORecords(List<UserCompanyDBO> newUserCompanyDBORecords) {
        StaffNotification.newUserCompanyDBORecords = newUserCompanyDBORecords;
    }

    public static void setDeserializedUserCompanyDBOList(List<UserCompanyDBO> deserializedUserCompanyDBOList) {
        StaffNotification.deserializedUserCompanyDBOList = deserializedUserCompanyDBOList;
    }

    @Override
    public void save() {
        deserializedUserCompanyDBOList.addAll(newUserCompanyDBORecords);
        serializeToFile(deserializedUserCompanyDBOList);}

    public void serializeToFile(List<UserCompanyDBO> userCompanyDBOS) {
        try {
            File file = new File(Constants.SESSION_SERIALIZATION_FILE_DIRECTORY + Session.getLoggedInUser().get().getId() + "/userCompany.dat");
            file.getParentFile().mkdirs();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));

            out.writeObject(userCompanyDBOS);
            out.close();
        } catch (IOException ex) {
            throw new SerializationException("Error while serializing company product to file", ex);
        }
    }

    public void deserializeFromFile() {
        newUserCompanyDBORecords.clear();

        File file = new File(Constants.SESSION_SERIALIZATION_FILE_DIRECTORY + Session.getLoggedInUser().get().getId() + "/userCompany.dat");
        if (file.length() == 0) {
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            setDeserializedUserCompanyDBOList((List<UserCompanyDBO>) in.readObject());
            deserializedUserCompanyDBOList.sort(Comparator.comparing(UserCompanyDBO::getCreatedAt));
        } catch (IOException | ClassNotFoundException ex) {
            throw new SerializationException("Error while serializing company product to file");
        }
    }

    public List<UserCompanyDBO> checkStaffChange() throws DatabaseConnectionActiveException {
        deserializeFromFile();

        UserCompanyDBO lastUserCompany = new UserCompanyDBO.Builder(0L)
                .userId(Session.getLoggedInUser().get().getId())
                .companyId(1L)
                .createdAt(LocalDateTime.of(1990, 1, 1, 0, 0))
                .build();

        if (!deserializedUserCompanyDBOList.isEmpty()) {
            lastUserCompany = deserializedUserCompanyDBOList.getLast();
        }

        List<UserCompanyDBO> userCompanyDBOS = new ArrayList<>();
        for (Company company : Session.getLoggedInUser().get().getCompanies()) {
            userCompanyDBOS.addAll(userCompanyRepository.findAllUserCompanyByUserId(lastUserCompany.getCreatedAt(),
                    company.getId()).stream().toList());
        }

        setNewUserCompanyDBORecords(userCompanyDBOS);
        return userCompanyDBOS;
    }
}
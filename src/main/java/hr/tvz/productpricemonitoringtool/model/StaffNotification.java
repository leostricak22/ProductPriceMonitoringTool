package hr.tvz.productpricemonitoringtool.model;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.SerializationException;
import hr.tvz.productpricemonitoringtool.model.dbo.UserCompanyDBO;
import hr.tvz.productpricemonitoringtool.repository.CompanyRepository;
import hr.tvz.productpricemonitoringtool.repository.UserCompanyRepository;
import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.Session;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public non-sealed class StaffNotification extends Notification  implements Serializable {

    public static List<UserCompanyDBO> deserializedUserCompanyDBOList = new ArrayList<>();
    public static List<UserCompanyDBO> newUserCompanyDBORecords = new ArrayList<>();

    private static final CompanyRepository companyRepository = new CompanyRepository();
    private final UserCompanyRepository userCompanyRepository = new UserCompanyRepository();

    public StaffNotification() {}

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
            deserializedUserCompanyDBOList = (List<UserCompanyDBO>) in.readObject();
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

        newUserCompanyDBORecords = userCompanyDBOS;
        return userCompanyDBOS;
    }
}
package hr.tvz.productpricemonitoringtool.model;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.SerializationException;
import hr.tvz.productpricemonitoringtool.model.dbo.CompanyProductDBO;
import hr.tvz.productpricemonitoringtool.repository.CompanyProductRepository;
import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.Session;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public non-sealed class PriceNotification extends Notification  implements Serializable {

    public static List<CompanyProductDBO> deserializedCompanyProductDBOList = new ArrayList<>();
    public static List<CompanyProductDBO> newCompanyProductRecords = new ArrayList<>();

    private static final CompanyProductRepository companyProductRepository = new CompanyProductRepository();

    public PriceNotification() {}

    @Override
    public void save() {
        deserializedCompanyProductDBOList.addAll(newCompanyProductRecords);
        serializeToFile(deserializedCompanyProductDBOList);
    }

    public void serializeToFile(List<CompanyProductDBO> companyProducts) {
        try {
            File file = new File(Constants.SESSION_SERIALIZATION_FILE_DIRECTORY + Session.getLoggedInUser().get().getId() + "/companyProducts.dat");
            file.getParentFile().mkdirs();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));

            out.writeObject(companyProducts);
            out.close();
        } catch (IOException ex) {
            throw new SerializationException("Error while serializing company product to file", ex);
        }
    }

    public void deserializeFromFile() {
        newCompanyProductRecords.clear();

        File file = new File(Constants.SESSION_SERIALIZATION_FILE_DIRECTORY + Session.getLoggedInUser().get().getId() + "/companyProducts.dat");
        if (file.length() == 0) {
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            deserializedCompanyProductDBOList = (List<CompanyProductDBO>) in.readObject();
            deserializedCompanyProductDBOList.sort(Comparator.comparing(CompanyProductDBO::getCreatedAt));
        } catch (IOException | ClassNotFoundException ex) {
            throw new SerializationException("Error while serializing company product to file");
        }
    }

    public List<CompanyProductDBO> checkPriceChange() throws DatabaseConnectionActiveException {
        deserializeFromFile();

        CompanyProductDBO lastCompanyProduct = new CompanyProductDBO.Builder(0L)
                .createdAt(LocalDateTime.of(1990, 1, 1, 0, 0))
                .build();

        if (!deserializedCompanyProductDBOList.isEmpty()) {
            lastCompanyProduct = deserializedCompanyProductDBOList.getLast();
        }

        List<CompanyProductDBO> companyProducts = new ArrayList<>();
        for (Company company : Session.getLoggedInUser().get().getCompanies()) {
            companyProducts.addAll(companyProductRepository.findByDateAndCompanyId(lastCompanyProduct.getCreatedAt(),
                            company.getId()).stream().toList());
        }

        newCompanyProductRecords = companyProducts;
        return companyProducts;
    }
}
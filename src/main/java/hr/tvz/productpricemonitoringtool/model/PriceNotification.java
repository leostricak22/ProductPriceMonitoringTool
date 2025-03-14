package hr.tvz.productpricemonitoringtool.model;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.SerializationException;
import hr.tvz.productpricemonitoringtool.model.dbo.CompanyProductDBO;
import hr.tvz.productpricemonitoringtool.repository.CompanyProductReadRepository;
import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Price notification model.
 * Represents the price notification in the model.
 */
public non-sealed class PriceNotification implements Notification {

    private static final Logger log = LoggerFactory.getLogger(PriceNotification.class);
    protected static List<CompanyProductDBO> deserializedCompanyProductDBOList = new ArrayList<>();
    protected static List<CompanyProductDBO> newCompanyProductRecords = new ArrayList<>();

    private final CompanyProductReadRepository companyProductReadRepository = new CompanyProductReadRepository();

    public PriceNotification() {
        log.info("Price Notification");
    }

    public static List<CompanyProductDBO> getNewCompanyProductRecords() {
        return newCompanyProductRecords;
    }

    /**
     * Save method.
     * Saves the new company product records to the file.
     * Adds the new company product records to the deserialized company product list.
     */
    @Override
    public void save() {
        deserializedCompanyProductDBOList.addAll(newCompanyProductRecords);
        serializeToFile(deserializedCompanyProductDBOList);
    }

    /**
     * Serialize to file method.
     * Serializes the company product to the file.
     * @param companyProducts List of company products.
     */
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

    /**
     * Deserialize from file method.
     * Deserializes the company product from the file.
     */
    public void deserializeFromFile() {
        newCompanyProductRecords.clear();

        File file = new File(Constants.SESSION_SERIALIZATION_FILE_DIRECTORY + Session.getLoggedInUser().get().getId() + "/companyProducts.dat");
        if (file.length() == 0) {
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            setDeserializedCompanyProductDBOList((List<CompanyProductDBO>) in.readObject());
            deserializedCompanyProductDBOList.sort(Comparator.comparing(CompanyProductDBO::getCreatedAt));
        } catch (IOException | ClassNotFoundException ex) {
            throw new SerializationException("Error while serializing company product to file");
        }
    }

    /**
     * Check price change method.
     * Checks the price change of the company products.
     */
    public static void setNewCompanyProductRecords(List<CompanyProductDBO> newCompanyProductRecords) {
        PriceNotification.newCompanyProductRecords = newCompanyProductRecords;
    }

    /**
     * Set deserialized company product DBO list method.
     * Sets the deserialized company product DBO list.
     */
    public static void setDeserializedCompanyProductDBOList(List<CompanyProductDBO> deserializedCompanyProductDBOList) {
        PriceNotification.deserializedCompanyProductDBOList = deserializedCompanyProductDBOList;
    }

    /**
     * Check price change method.
     * Checks the price change of the company products.
     * @return List of company products.
     */
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
            companyProducts.addAll(companyProductReadRepository.findByDateAndCompanyId(lastCompanyProduct.getCreatedAt(),
                            company.getId()).stream().toList());
        }

        setNewCompanyProductRecords(companyProducts);
        return companyProducts;
    }
}
package com.example.demo.customer.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.customer.domain.Customer;
import com.example.demo.customer.dto.CustomerStagingRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:customer_mapper_test;MODE=DB2;DATABASE_TO_UPPER=true;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.hikari.connection-timeout=5000",
        "spring.datasource.hikari.validation-timeout=3000",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/customer-schema-db2.sql"
})
@Transactional
@Timeout(30)
class CustomerMapperIntegrationTest {

    @Autowired
    private CustomerMapper customerMapper;

    @Test
    void findAllReturnsCustomersOrderedByNewestIdFirst() {
        Customer first = insertCustomer("Mapper FindAll First");
        Customer second = insertCustomer("Mapper FindAll Second");

        List<Customer> customers = customerMapper.findAll();
        int secondIndex = indexOf(customers, second.getId());
        int firstIndex = indexOf(customers, first.getId());

        assertThat(customers).extracting(Customer::getId)
                .isSortedAccordingTo(Comparator.reverseOrder());
        assertThat(secondIndex).isNotNegative();
        assertThat(firstIndex).isNotNegative();
        assertThat(secondIndex).isLessThan(firstIndex);
    }

    @Test
    void insertFindByIdFindByEmailAndSearchMapCustomerColumns() {
        Customer customer = insertCustomer("Mapper Search Target");
        String emailLocalPart = customer.getEmail().substring(0, customer.getEmail().indexOf('@'));

        Customer foundById = customerMapper.findById(customer.getId());
        Customer foundByEmail = customerMapper.findByEmail(customer.getEmail());
        List<Customer> nameAndEmailMatches = customerMapper.search("search target", emailLocalPart);
        List<Customer> emailMatches = customerMapper.search(null, emailLocalPart);
        List<Customer> noConditionMatches = customerMapper.search("", "");

        assertCustomer(foundById, customer);
        assertCustomer(foundByEmail, customer);
        assertThat(nameAndEmailMatches).extracting(Customer::getId).contains(customer.getId());
        assertThat(emailMatches).extracting(Customer::getId).contains(customer.getId());
        assertThat(noConditionMatches).extracting(Customer::getId).contains(customer.getId());
        assertThat(foundById.getCreatedAt()).isNotNull();
        assertThat(foundById.getUpdatedAt()).isNotNull();
    }

    @Test
    void mergeByEmailInsertsAndUpdatesCustomer() {
        Customer insertTarget = newCustomer("Mapper Merge Insert");

        int insertedRows = customerMapper.mergeByEmail(insertTarget);
        Customer inserted = customerMapper.findByEmail(insertTarget.getEmail());

        assertThat(insertedRows).isEqualTo(1);
        assertThat(inserted).isNotNull();
        assertThat(inserted.getName()).isEqualTo("Mapper Merge Insert");

        Customer updateTarget = new Customer();
        updateTarget.setName("Mapper Merge Updated");
        updateTarget.setEmail(insertTarget.getEmail());
        updateTarget.setPhoneNumber("010-9999-0000");

        int updatedRows = customerMapper.mergeByEmail(updateTarget);
        Customer updated = customerMapper.findByEmail(insertTarget.getEmail());

        assertThat(updatedRows).isEqualTo(1);
        assertThat(updated.getId()).isEqualTo(inserted.getId());
        assertThat(updated.getName()).isEqualTo("Mapper Merge Updated");
        assertThat(updated.getPhoneNumber()).isEqualTo("010-9999-0000");
    }

    @Test
    void insertCopyFromCustomerCopiesSourceCustomerWithNewEmail() {
        Customer source = insertCustomer("Mapper Copy Source");
        String copiedEmail = uniqueEmail("mapper-copy");

        int insertedRows = customerMapper.insertCopyFromCustomer(source.getId(), copiedEmail);
        Customer copied = customerMapper.findByEmail(copiedEmail);

        assertThat(insertedRows).isEqualTo(1);
        assertThat(copied).isNotNull();
        assertThat(copied.getId()).isNotEqualTo(source.getId());
        assertThat(copied.getName()).isEqualTo(source.getName());
        assertThat(copied.getEmail()).isEqualTo(copiedEmail);
        assertThat(copied.getPhoneNumber()).isEqualTo(source.getPhoneNumber());
    }

    @Test
    void insertCopyFromCustomerReturnsZeroWhenSourceDoesNotExist() {
        int insertedRows = customerMapper.insertCopyFromCustomer(-1L, uniqueEmail("missing-copy"));

        assertThat(insertedRows).isZero();
    }

    @Test
    void insertStagingBatchAndInsertFromStagingImportOnlyNewEmails() {
        Customer existing = insertCustomer("Mapper Staging Existing");
        String importStatus = uniqueStatus();
        CustomerStagingRequest first = stagingCustomer("Mapper Staging First");
        CustomerStagingRequest second = stagingCustomer("Mapper Staging Second");
        CustomerStagingRequest duplicate = new CustomerStagingRequest(
                "Mapper Staging Duplicate",
                existing.getEmail(),
                "010-2222-3333"
        );

        int stagedRows = customerMapper.insertStagingBatch(List.of(first, second, duplicate), importStatus);
        int importedRows = customerMapper.insertFromStaging(importStatus);

        assertThat(stagedRows).isEqualTo(3);
        assertThat(importedRows).isEqualTo(2);
        assertThat(customerMapper.findByEmail(first.email())).isNotNull();
        assertThat(customerMapper.findByEmail(second.email())).isNotNull();
        assertThat(customerMapper.findByEmail(existing.getEmail()).getName()).isEqualTo(existing.getName());
    }

    @Test
    void updateChangesCustomerColumns() {
        Customer customer = insertCustomer("Mapper Update Before");
        customer.setName("Mapper Update After");
        customer.setEmail(uniqueEmail("mapper-update-after"));
        customer.setPhoneNumber("010-7777-8888");

        int updatedRows = customerMapper.update(customer);
        Customer updated = customerMapper.findById(customer.getId());

        assertThat(updatedRows).isEqualTo(1);
        assertCustomer(updated, customer);
        assertThat(updated.getUpdatedAt()).isNotNull();
    }

    @Test
    void deleteByIdRemovesCustomer() {
        Customer customer = insertCustomer("Mapper Delete Target");

        int deletedRows = customerMapper.deleteById(customer.getId());

        assertThat(deletedRows).isEqualTo(1);
        assertThat(customerMapper.findById(customer.getId())).isNull();
    }

    private Customer insertCustomer(String name) {
        Customer customer = newCustomer(name);
        int insertedRows = customerMapper.insert(customer);

        assertThat(insertedRows).isEqualTo(1);
        assertThat(customer.getId()).isNotNull();
        return customer;
    }

    private Customer newCustomer(String name) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(uniqueEmail(name));
        customer.setPhoneNumber("010-1234-5678");
        return customer;
    }

    private CustomerStagingRequest stagingCustomer(String name) {
        return new CustomerStagingRequest(name, uniqueEmail(name), "010-1111-2222");
    }

    private String uniqueEmail(String prefix) {
        String normalizedPrefix = prefix.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "-")
                .replaceAll("-+", "-")
                .replaceAll("(^-|-$)", "");
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return normalizedPrefix + "." + suffix + "@example.com";
    }

    private String uniqueStatus() {
        return "T" + UUID.randomUUID().toString().replace("-", "").substring(0, 19);
    }

    private int indexOf(List<Customer> customers, Long id) {
        for (int index = 0; index < customers.size(); index++) {
            if (id.equals(customers.get(index).getId())) {
                return index;
            }
        }
        return -1;
    }

    private void assertCustomer(Customer actual, Customer expected) {
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getPhoneNumber()).isEqualTo(expected.getPhoneNumber());
    }
}

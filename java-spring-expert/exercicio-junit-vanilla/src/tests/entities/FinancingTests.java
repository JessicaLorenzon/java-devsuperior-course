package tests.entities;

import entities.Financing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tests.factory.FinancingFactory;

public class FinancingTests {

    @Test
    public void constructorShouldCreateObjectWhenValidData() {
        Financing f = FinancingFactory.createFinancingValid();

        Assertions.assertEquals(100000.0, f.getTotalAmount());
        Assertions.assertEquals(2000.0, f.getIncome());
        Assertions.assertEquals(80, f.getMonths());
    }

    @Test
    public void constructorShouldThrowIllegalArgumentExceptionWhenInvalidData() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FinancingFactory.createFinancingInvalid());
    }

    @Test
    public void setTotalAmountShouldUpdateDataWhenValidData() {
        Financing f = FinancingFactory.createFinancingValid();

        f.setTotalAmount(90000.0);

        Assertions.assertEquals(90000.0, f.getTotalAmount());
    }

    @Test
    public void setTotalAmountShouldThrowIllegalArgumentExceptionWhenInvalidData() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Financing f = FinancingFactory.createFinancingValid();
            f.setTotalAmount(110000.0);
        });
    }

    @Test
    public void setIncomeShouldUpdateDataWhenValidData() {
        Financing f = FinancingFactory.createFinancingValid();

        f.setIncome(3000.0);

        Assertions.assertEquals(3000.0, f.getIncome());
    }

    @Test
    public void setIncomeShouldThrowIllegalArgumentExceptionWhenInvalidData() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Financing f = FinancingFactory.createFinancingValid();
            f.setIncome(1000.0);
        });
    }

    @Test
    public void setMonthsShouldUpdateDataWhenValidData() {
        Financing f = FinancingFactory.createFinancingValid();

        f.setMonths(90);

        Assertions.assertEquals(90, f.getMonths());
    }

    @Test
    public void setMonthsShouldThrowIllegalArgumentExceptionWhenInvalidData() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Financing f = FinancingFactory.createFinancingValid();
            f.setMonths(70);
        });
    }

    @Test
    public void entryShouldCalculateEntryCorrectly() {
        Financing f = FinancingFactory.createFinancingValid();

        Assertions.assertEquals(20000.0, f.entry());
    }

    @Test
    public void quotaShouldCalculateQuotaCorrectly() {
        Financing f = FinancingFactory.createFinancingValid();

        Assertions.assertEquals(1000.0, f.quota());
    }
}

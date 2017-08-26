package tk.vxmvconverter.app.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import tk.vxmvconverter.app.domain.Conversion;
import tk.vxmvconverter.app.domain.ConversionDao;
import tk.vxmvconverter.app.domain.Status;
import tk.vxmvconverter.app.exception.ConverterException;
import tk.vxmvconverter.app.exception.Error;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ConversionServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    ConversionDao conversionDao;
    ConversionService conversionService;

    @Before
    public void setUp() throws Exception {
        conversionDao = mock(ConversionDao.class);
        conversionService = spy(ConversionService.class);
        conversionService.setConversionDao(conversionDao);
    }

    @Test
    public void save() throws Exception {
        Conversion c1 = new Conversion();
        c1.setUuid(UUID.randomUUID().toString());
        when(conversionDao.save(any(Conversion.class))).thenReturn(c1);
        assertEquals(conversionService.save("BLOB FINTO".getBytes(), null, null), c1.getUuid());
        verify(conversionDao).save(any(Conversion.class));
    }

    @Test
    public void saveOnErrorWrapsException() throws Exception {
        expectedException.expect(ConverterException.class);
        expectedException.expect(hasProperty("error", equalTo(Error.SAVING_ERROR)));
        conversionService.save(null, null, null, null);
    }

    @Test
    public void updateShallGoWhenSynchronized() throws Exception {
        Conversion c1 = new Conversion();
        c1.setUuid(UUID.randomUUID().toString());
        c1.setLastEdit(ZonedDateTime.now().minusDays(1));
        c1.setStatus(Status.PROCESSING);
        Conversion c2 = new Conversion();
        c2.setUuid(c1.getUuid());
        c2.setLastEdit(c1.getLastEdit());
        c2.setStatus(Status.RECEIVED);

        when(conversionDao.findOne(c1.getUuid())).thenReturn(c2);

        int expect = 0;

        conversionService.update(c1);
        verify(conversionService, times(++expect)).get(c1.getUuid());
        verify(conversionDao, times(expect)).save(c1);

        c1.setStatus(Status.PROCESSED);
        c2.setStatus(Status.PROCESSING);

        conversionService.update(c1);
        verify(conversionService, times(++expect)).get(c1.getUuid());
        verify(conversionDao, times(expect)).save(c1);

        c1.setStatus(Status.PROCESSED);
        c2.setStatus(Status.PROCESSED);

        conversionService.update(c1);
        verify(conversionService, times(++expect)).get(c1.getUuid());
        verify(conversionDao, times(expect)).save(c1);

        c1.setStatus(Status.PROCESSING);
        c2.setStatus(Status.RECEIVED);

        conversionService.update(c1);
        verify(conversionService, times(++expect)).get(c1.getUuid());
        verify(conversionDao, times(expect)).save(c1);
    }

    @Test
    public void updateShouldFailOnWrongStateForProcessed() throws Exception {
        Conversion c1 = new Conversion();
        c1.setUuid(UUID.randomUUID().toString());
        c1.setLastEdit(ZonedDateTime.now().minusDays(1));
        c1.setStatus(Status.PROCESSING);
        Conversion c2 = new Conversion();
        c2.setUuid(c1.getUuid());
        c2.setLastEdit(c1.getLastEdit());
        c2.setStatus(Status.PROCESSED);

        when(conversionDao.findOne(c1.getUuid())).thenReturn(c2);

        expectedException.expect(ConverterException.class);
        expectedException.expect(hasProperty("error", equalTo(Error.CONVERSION_IN_WRONG_STATE)));
        conversionService.update(c1);
    }

    @Test
    public void updateShouldFailOnWrongStateForProcessing() throws Exception {
        Conversion c1 = new Conversion();
        c1.setUuid(UUID.randomUUID().toString());
        c1.setLastEdit(ZonedDateTime.now().minusDays(1));
        c1.setStatus(Status.RECEIVED);
        Conversion c2 = new Conversion();
        c2.setUuid(c1.getUuid());
        c2.setLastEdit(c1.getLastEdit());
        c2.setStatus(Status.PROCESSING);

        when(conversionDao.findOne(c1.getUuid())).thenReturn(c2);

        expectedException.expect(ConverterException.class);
        expectedException.expect(hasProperty("error", equalTo(Error.CONVERSION_IN_WRONG_STATE)));
        conversionService.update(c1);
    }

    @Test
    public void updateShouldFailOnWrongStateForReceived() throws Exception {
        Conversion c1 = new Conversion();
        c1.setUuid(UUID.randomUUID().toString());
        c1.setLastEdit(ZonedDateTime.now().minusDays(1));
        c1.setStatus(Status.PROCESSED);
        Conversion c2 = new Conversion();
        c2.setUuid(c1.getUuid());
        c2.setLastEdit(c1.getLastEdit());
        c2.setStatus(Status.RECEIVED);

        when(conversionDao.findOne(c1.getUuid())).thenReturn(c2);

        expectedException.expect(ConverterException.class);
        expectedException.expect(hasProperty("error", equalTo(Error.CONVERSION_IN_WRONG_STATE)));
        conversionService.update(c1);
    }

    @Test
    public void updateMustFailForNonSynchronized() throws Exception {
        Conversion c1 = new Conversion();
        c1.setUuid(UUID.randomUUID().toString());
        c1.setLastEdit(ZonedDateTime.now().minusDays(1));
        Conversion c2 = new Conversion();
        c2.setUuid(c1.getUuid());
        c2.setLastEdit(ZonedDateTime.now());

        when(conversionDao.findOne(c1.getUuid())).thenReturn(c2);

        expectedException.expect(ConverterException.class);
        expectedException.expect(hasProperty("error", equalTo(Error.CONCURRENCY_ERROR)));
        conversionService.update(c1);
    }

    @Test
    public void updateMustFailForNonExistent() throws Exception {
        Conversion c1 = new Conversion();
        c1.setUuid(UUID.randomUUID().toString());
        c1.setLastEdit(ZonedDateTime.now().minusDays(1));
        expectedException.expect(ConverterException.class);
        expectedException.expect(hasProperty("error", equalTo(Error.CANNOT_FIND_CONVERSION_REQUEST)));
        conversionService.update(c1);
    }

    @Test
    public void deleteMustFailForUuidNotFound() throws Exception {
        expectedException.expect(ConverterException.class);
        expectedException.expect(hasProperty("error", equalTo(Error.CANNOT_FIND_CONVERSION_REQUEST)));
        conversionService.delete("UUID-FAKE");
    }

    @Test
    public void deleteShouldComplete() throws Exception {
        Conversion conversion = new Conversion();
        conversion.setUuid(UUID.randomUUID().toString());
        when(conversionDao.findOne(conversion.getUuid())).thenReturn(conversion);
        conversionService.delete(conversion.getUuid());
        verify(conversionDao).delete(conversion.getUuid());
        verify(conversionService).get(conversion.getUuid());
    }

    @Test
    public void get() throws Exception {
        conversionService.get("UUID");
        verify(conversionDao).findOne(eq("UUID"));
    }

}
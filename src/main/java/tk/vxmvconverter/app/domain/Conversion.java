package tk.vxmvconverter.app.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Blob;
import java.time.ZonedDateTime;

@Entity
@Table(name = "conversion")
public class Conversion {
    private String uuid;
    private ZonedDateTime conversionRequest;
    private ZonedDateTime conversionCompleted;
    private ZonedDateTime lastEdit;
    private Blob data;
    private DestinationVersion destinationVersion;
    private ElementType elementType;
    private Status status;
    private String email;
    private Boolean isInError;

    @Id
    @Column(name = "uuid")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Column(name = "conversion_request")
    public ZonedDateTime getConversionRequest() {
        return conversionRequest;
    }

    public void setConversionRequest(ZonedDateTime conversionRequest) {
        this.conversionRequest = conversionRequest;
    }

    @Column(name = "conversion_completed")
    public ZonedDateTime getConversionCompleted() {
        return conversionCompleted;
    }

    public void setConversionCompleted(ZonedDateTime conversionCompleted) {
        this.conversionCompleted = conversionCompleted;
    }

    @Column(name = "last_edit")
    public ZonedDateTime getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(ZonedDateTime lastEdit) {
        this.lastEdit = lastEdit;
    }

    @Column(name = "data")
    public Blob getData() {
        return data;
    }

    public void setData(Blob data) {
        this.data = data;
    }

    @Column(name = "destination_version")
    public DestinationVersion getDestinationVersion() {
        return destinationVersion;
    }

    public void setDestinationVersion(DestinationVersion destinationVersion) {
        this.destinationVersion = destinationVersion;
    }

    @Column(name = "element_type")
    public ElementType getElementType() {
        return elementType;
    }

    public void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }

    @Column(name = "status")
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "isInError")
    public Boolean getInError() {
        return isInError;
    }

    public void setInError(Boolean inError) {
        isInError = inError;
    }
}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.05.21 at 09:59:49 PM BST 
//


package mismo;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AUS_REOPropertyDispositionStatusTypeEnumerated.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AUS_REOPropertyDispositionStatusTypeEnumerated">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PendingSale"/>
 *     &lt;enumeration value="RetainForRental"/>
 *     &lt;enumeration value="RetainForPrimaryOrSecondaryResidence"/>
 *     &lt;enumeration value="Sold"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AUS_REOPropertyDispositionStatusTypeEnumerated")
@XmlEnum
public enum AUSREOPropertyDispositionStatusTypeEnumerated {

    @XmlEnumValue("PendingSale")
    PENDING_SALE("PendingSale"),
    @XmlEnumValue("RetainForRental")
    RETAIN_FOR_RENTAL("RetainForRental"),
    @XmlEnumValue("RetainForPrimaryOrSecondaryResidence")
    RETAIN_FOR_PRIMARY_OR_SECONDARY_RESIDENCE("RetainForPrimaryOrSecondaryResidence"),
    @XmlEnumValue("Sold")
    SOLD("Sold");
    private final String value;

    AUSREOPropertyDispositionStatusTypeEnumerated(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AUSREOPropertyDispositionStatusTypeEnumerated fromValue(String v) {
        for (AUSREOPropertyDispositionStatusTypeEnumerated c: AUSREOPropertyDispositionStatusTypeEnumerated.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
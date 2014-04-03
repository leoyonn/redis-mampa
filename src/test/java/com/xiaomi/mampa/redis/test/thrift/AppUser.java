/**
 * AppUser.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Apr 2, 2014 7:40:02 PM
 */

package com.xiaomi.mampa.redis.test.thrift;

import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TBase;
import org.apache.thrift.TBaseHelper;
import org.apache.thrift.TException;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TFieldRequirementType;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.protocol.TType;

/**
 * Just used for unit test.
 * 
 * @author leo
 */
public class AppUser implements TBase<AppUser, AppUser._Fields>, java.io.Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    private static final TStruct STRUCT_DESC = new TStruct("AppUser");

    private static final TField USER_ID_FIELD_DESC = new TField("userId", TType.STRING, (short) 1);
    private static final TField USER_DOMAIN_FIELD_DESC = new TField("userDomain", TType.STRING, (short) 2);
    private static final TField APP_ID_FIELD_DESC = new TField("appId", TType.I64, (short) 3);
    private static final TField APP_KEY_FIELD_DESC = new TField("appKey", TType.STRING, (short) 4);

    public String userId;
    public String userDomain;
    public long appId;
    public String appKey;

    /**
     * The set of fields this struct contains, along with convenience methods for finding and manipulating
     * them.
     */
    public enum _Fields implements TFieldIdEnum {
        USER_ID((short) 1, "userId"), USER_DOMAIN((short) 2, "userDomain"), APP_ID((short) 3, "appId"), APP_KEY(
                (short) 4, "appKey");

        private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

        static {
            for (_Fields field : EnumSet.allOf(_Fields.class)) {
                byName.put(field.getFieldName(), field);
            }
        }

        /**
         * Find the _Fields constant that matches fieldId, or null if its not found.
         */
        public static _Fields findByThriftId(int fieldId) {
            switch (fieldId) {
                case 1: // USER_ID
                    return USER_ID;
                case 2: // USER_DOMAIN
                    return USER_DOMAIN;
                case 3: // APP_ID
                    return APP_ID;
                case 4: // APP_KEY
                    return APP_KEY;
                default:
                    return null;
            }
        }

        /**
         * Find the _Fields constant that matches fieldId, throwing an exception
         * if it is not found.
         */
        public static _Fields findByThriftIdOrThrow(int fieldId) {
            _Fields fields = findByThriftId(fieldId);
            if (fields == null)
                throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
            return fields;
        }

        /**
         * Find the _Fields constant that matches name, or null if its not found.
         */
        public static _Fields findByName(String name) {
            return byName.get(name);
        }

        private final short _thriftId;
        private final String _fieldName;

        _Fields(short thriftId, String fieldName) {
            _thriftId = thriftId;
            _fieldName = fieldName;
        }

        public short getThriftFieldId() {
            return _thriftId;
        }

        public String getFieldName() {
            return _fieldName;
        }
    }

    // isset id assignments
    private static final int __APPID_ISSET_ID = 0;
    private BitSet __isset_bit_vector = new BitSet(1);

    public static final Map<_Fields, FieldMetaData> metaDataMap;
    static {
        Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.USER_ID, new FieldMetaData("userId", TFieldRequirementType.REQUIRED, new FieldValueMetaData(
                TType.STRING)));
        tmpMap.put(_Fields.USER_DOMAIN, new FieldMetaData("userDomain", TFieldRequirementType.REQUIRED,
                new FieldValueMetaData(TType.STRING)));
        tmpMap.put(_Fields.APP_ID, new FieldMetaData("appId", TFieldRequirementType.REQUIRED, new FieldValueMetaData(
                TType.I64)));
        tmpMap.put(_Fields.APP_KEY, new FieldMetaData("appKey", TFieldRequirementType.OPTIONAL, new FieldValueMetaData(
                TType.STRING)));
        metaDataMap = Collections.unmodifiableMap(tmpMap);
        FieldMetaData.addStructMetaDataMap(AppUser.class, metaDataMap);
    }

    public AppUser() {
    }

    public AppUser(String userId, String userDomain, long appId) {
        this();
        this.userId = userId;
        this.userDomain = userDomain;
        this.appId = appId;
        setAppIdIsSet(true);
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public AppUser(AppUser other) {
        __isset_bit_vector.clear();
        __isset_bit_vector.or(other.__isset_bit_vector);
        if (other.isSetUserId()) {
            this.userId = other.userId;
        }
        if (other.isSetUserDomain()) {
            this.userDomain = other.userDomain;
        }
        this.appId = other.appId;
        if (other.isSetAppKey()) {
            this.appKey = other.appKey;
        }
    }

    public AppUser deepCopy() {
        return new AppUser(this);
    }

    @Override
    public void clear() {
        this.userId = null;
        this.userDomain = null;
        setAppIdIsSet(false);
        this.appId = 0;
        this.appKey = null;
    }

    public String getUserId() {
        return this.userId;
    }

    public AppUser setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public void unsetUserId() {
        this.userId = null;
    }

    /** Returns true if field userId is set (has been asigned a value) and false otherwise */
    public boolean isSetUserId() {
        return this.userId != null;
    }

    public void setUserIdIsSet(boolean value) {
        if (!value) {
            this.userId = null;
        }
    }

    public String getUserDomain() {
        return this.userDomain;
    }

    public AppUser setUserDomain(String userDomain) {
        this.userDomain = userDomain;
        return this;
    }

    public void unsetUserDomain() {
        this.userDomain = null;
    }

    /** Returns true if field userDomain is set (has been asigned a value) and false otherwise */
    public boolean isSetUserDomain() {
        return this.userDomain != null;
    }

    public void setUserDomainIsSet(boolean value) {
        if (!value) {
            this.userDomain = null;
        }
    }

    public long getAppId() {
        return this.appId;
    }

    public AppUser setAppId(long appId) {
        this.appId = appId;
        setAppIdIsSet(true);
        return this;
    }

    public void unsetAppId() {
        __isset_bit_vector.clear(__APPID_ISSET_ID);
    }

    /** Returns true if field appId is set (has been asigned a value) and false otherwise */
    public boolean isSetAppId() {
        return __isset_bit_vector.get(__APPID_ISSET_ID);
    }

    public void setAppIdIsSet(boolean value) {
        __isset_bit_vector.set(__APPID_ISSET_ID, value);
    }

    public String getAppKey() {
        return this.appKey;
    }

    public AppUser setAppKey(String appKey) {
        this.appKey = appKey;
        return this;
    }

    public void unsetAppKey() {
        this.appKey = null;
    }

    /** Returns true if field appKey is set (has been asigned a value) and false otherwise */
    public boolean isSetAppKey() {
        return this.appKey != null;
    }

    public void setAppKeyIsSet(boolean value) {
        if (!value) {
            this.appKey = null;
        }
    }

    public void setFieldValue(_Fields field, Object value) {
        switch (field) {
            case USER_ID:
                if (value == null) {
                    unsetUserId();
                } else {
                    setUserId((String) value);
                }
                break;

            case USER_DOMAIN:
                if (value == null) {
                    unsetUserDomain();
                } else {
                    setUserDomain((String) value);
                }
                break;

            case APP_ID:
                if (value == null) {
                    unsetAppId();
                } else {
                    setAppId((Long) value);
                }
                break;

            case APP_KEY:
                if (value == null) {
                    unsetAppKey();
                } else {
                    setAppKey((String) value);
                }
                break;

        }
    }

    public Object getFieldValue(_Fields field) {
        switch (field) {
            case USER_ID:
                return getUserId();

            case USER_DOMAIN:
                return getUserDomain();

            case APP_ID:
                return new Long(getAppId());

            case APP_KEY:
                return getAppKey();

        }
        throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been asigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
        if (field == null) {
            throw new IllegalArgumentException();
        }

        switch (field) {
            case USER_ID:
                return isSetUserId();
            case USER_DOMAIN:
                return isSetUserDomain();
            case APP_ID:
                return isSetAppId();
            case APP_KEY:
                return isSetAppKey();
        }
        throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
        if (that == null)
            return false;
        if (that instanceof AppUser)
            return this.equals((AppUser) that);
        return false;
    }

    public boolean equals(AppUser that) {
        if (that == null)
            return false;

        boolean this_present_userId = true && this.isSetUserId();
        boolean that_present_userId = true && that.isSetUserId();
        if (this_present_userId || that_present_userId) {
            if (!(this_present_userId && that_present_userId))
                return false;
            if (!this.userId.equals(that.userId))
                return false;
        }

        boolean this_present_userDomain = true && this.isSetUserDomain();
        boolean that_present_userDomain = true && that.isSetUserDomain();
        if (this_present_userDomain || that_present_userDomain) {
            if (!(this_present_userDomain && that_present_userDomain))
                return false;
            if (!this.userDomain.equals(that.userDomain))
                return false;
        }

        boolean this_present_appId = true;
        boolean that_present_appId = true;
        if (this_present_appId || that_present_appId) {
            if (!(this_present_appId && that_present_appId))
                return false;
            if (this.appId != that.appId)
                return false;
        }

        boolean this_present_appKey = true && this.isSetAppKey();
        boolean that_present_appKey = true && that.isSetAppKey();
        if (this_present_appKey || that_present_appKey) {
            if (!(this_present_appKey && that_present_appKey))
                return false;
            if (!this.appKey.equals(that.appKey))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public int compareTo(AppUser other) {
        if (!getClass().equals(other.getClass())) {
            return getClass().getName().compareTo(other.getClass().getName());
        }

        int lastComparison = 0;
        AppUser typedOther = (AppUser) other;

        lastComparison = Boolean.valueOf(isSetUserId()).compareTo(typedOther.isSetUserId());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetUserId()) {
            lastComparison = TBaseHelper.compareTo(this.userId, typedOther.userId);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(isSetUserDomain()).compareTo(typedOther.isSetUserDomain());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetUserDomain()) {
            lastComparison = TBaseHelper.compareTo(this.userDomain, typedOther.userDomain);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(isSetAppId()).compareTo(typedOther.isSetAppId());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetAppId()) {
            lastComparison = TBaseHelper.compareTo(this.appId, typedOther.appId);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(isSetAppKey()).compareTo(typedOther.isSetAppKey());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetAppKey()) {
            lastComparison = TBaseHelper.compareTo(this.appKey, typedOther.appKey);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        return 0;
    }

    public _Fields fieldForId(int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }

    public void read(TProtocol iprot) throws TException {
        TField field;
        iprot.readStructBegin();
        while (true) {
            field = iprot.readFieldBegin();
            if (field.type == TType.STOP) {
                break;
            }
            switch (field.id) {
                case 1: // USER_ID
                    if (field.type == TType.STRING) {
                        this.userId = iprot.readString();
                    } else {
                        TProtocolUtil.skip(iprot, field.type);
                    }
                    break;
                case 2: // USER_DOMAIN
                    if (field.type == TType.STRING) {
                        this.userDomain = iprot.readString();
                    } else {
                        TProtocolUtil.skip(iprot, field.type);
                    }
                    break;
                case 3: // APP_ID
                    if (field.type == TType.I64) {
                        this.appId = iprot.readI64();
                        setAppIdIsSet(true);
                    } else {
                        TProtocolUtil.skip(iprot, field.type);
                    }
                    break;
                case 4: // APP_KEY
                    if (field.type == TType.STRING) {
                        this.appKey = iprot.readString();
                    } else {
                        TProtocolUtil.skip(iprot, field.type);
                    }
                    break;
                default:
                    TProtocolUtil.skip(iprot, field.type);
            }
            iprot.readFieldEnd();
        }
        iprot.readStructEnd();

        // check for required fields of primitive type, which can't be checked in the validate method
        if (!isSetAppId()) {
            throw new TProtocolException("Required field 'appId' was not found in serialized data! Struct: "
                    + toString());
        }
        validate();
    }

    public void write(TProtocol oprot) throws TException {
        validate();

        oprot.writeStructBegin(STRUCT_DESC);
        if (this.userId != null) {
            oprot.writeFieldBegin(USER_ID_FIELD_DESC);
            oprot.writeString(this.userId);
            oprot.writeFieldEnd();
        }
        if (this.userDomain != null) {
            oprot.writeFieldBegin(USER_DOMAIN_FIELD_DESC);
            oprot.writeString(this.userDomain);
            oprot.writeFieldEnd();
        }
        oprot.writeFieldBegin(APP_ID_FIELD_DESC);
        oprot.writeI64(this.appId);
        oprot.writeFieldEnd();
        if (this.appKey != null) {
            if (isSetAppKey()) {
                oprot.writeFieldBegin(APP_KEY_FIELD_DESC);
                oprot.writeString(this.appKey);
                oprot.writeFieldEnd();
            }
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AppUser(");
        boolean first = true;

        sb.append("userId:");
        if (this.userId == null) {
            sb.append("null");
        } else {
            sb.append(this.userId);
        }
        first = false;
        if (!first)
            sb.append(", ");
        sb.append("userDomain:");
        if (this.userDomain == null) {
            sb.append("null");
        } else {
            sb.append(this.userDomain);
        }
        first = false;
        if (!first)
            sb.append(", ");
        sb.append("appId:");
        sb.append(this.appId);
        first = false;
        if (isSetAppKey()) {
            if (!first)
                sb.append(", ");
            sb.append("appKey:");
            if (this.appKey == null) {
                sb.append("null");
            } else {
                sb.append(this.appKey);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }

    public void validate() throws TException {
        // check for required fields
        if (userId == null) {
            throw new TProtocolException("Required field 'userId' was not present! Struct: " + toString());
        }
        if (userDomain == null) {
            throw new TProtocolException("Required field 'userDomain' was not present! Struct: " + toString());
        }
        // alas, we cannot check 'appId' because it's a primitive and you chose the non-beans generator.
    }
}

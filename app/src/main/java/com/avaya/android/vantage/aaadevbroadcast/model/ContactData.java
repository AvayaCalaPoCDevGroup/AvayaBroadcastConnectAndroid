package com.avaya.android.vantage.aaadevbroadcast.model;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.avaya.android.vantage.aaadevbroadcast.R;
import com.avaya.android.vantage.aaadevbroadcast.csdk.LocalContactInfo;
import com.avaya.android.vantage.aaadevbroadcast.fragments.OnContactInteractionListener;
import com.avaya.clientservices.contact.Contact;
import com.avaya.clientservices.contact.ContactProviderSourceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * An Object Representing contact data.
 */
public class ContactData implements Parcelable {

    private ContactData(Parcel in) {
        mName = in.readString();
        mFirstName = in.readString();
        mLastName = in.readString();
        mPhoto = in.createByteArray();
        setIsFavorite(in.readByte() != 0);
        mLocation = in.readString();
        mCity = in.readString();
        mCompany = in.readString();
        mPosition = in.readString();
        mUUID = in.readString();
        mIsHeader = in.readByte() != 0;
        mIsPending = in.readByte() != 0;
        mCategory = Category.fromId(in.readInt());
        mPhones = new ArrayList<>();
        in.readTypedList(mPhones, PhoneNumber.CREATOR);

        mURI = in.readString();
        mPhotoThumbnailURI = in.readString();
        setIsFavorite(in.readByte() != 0);
        mEmail = in.readString();
        mPhotoURI = in.readString();
        mAccountType = in.readString();
        mDirectoryName = in.readString();
        mAccountName = in.readString();
        mDirectoryID = in.readString();
    }

    public static final Creator<ContactData> CREATOR = new Creator<ContactData>() {
        @Override
        public ContactData createFromParcel(Parcel in) {
            return new ContactData(in);
        }

        @Override
        public ContactData[] newArray(int size) {
            return new ContactData[size];
        }
    };

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mName);
        dest.writeString(mFirstName);
        dest.writeString(mLastName);
        dest.writeByteArray(mPhoto);
        dest.writeByte((byte) (mIsFavorite ? 1 : 0));
        dest.writeString(mLocation);
        dest.writeString(mCity);
        dest.writeString(mCompany);
        dest.writeString(mPosition);
        dest.writeString(mUUID);
        dest.writeByte((byte) (mIsHeader ? 1 : 0));
        dest.writeByte((byte) (mIsPending ? 1 : 0));
        dest.writeInt(mCategory.ordinal());
        if (mPhones != null) {
            dest.writeTypedList(mPhones);
        } else {
            dest.writeTypedList(new ArrayList<PhoneNumber>());
        }
        dest.writeString(mURI);
        dest.writeString(mPhotoThumbnailURI);
        dest.writeByte((byte) (mHasPhone ? 1 : 0));
        dest.writeString(mEmail);
        dest.writeString(mPhotoURI);
        dest.writeString(mAccountType);
        dest.writeString(mDirectoryName);
        dest.writeString(mAccountName);
        dest.writeString(mDirectoryID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactData)) return false;
        ContactData that = (ContactData) o;
        return Objects.equals(mName, that.mName) &&
                Objects.equals(mFirstName, that.mFirstName) &&
                Objects.equals(mLastName, that.mLastName) &&
                Arrays.equals(mPhoto, that.mPhoto) &&
                Objects.equals(mIsFavorite, that.mIsFavorite) &&
                Objects.equals(mLocation, that.mLocation) &&
                Objects.equals(mCity, that.mCity) &&
                Objects.equals(mCompany, that.mCompany) &&
                Objects.equals(mPosition, that.mPosition) &&
                Objects.equals(mPhones, that.mPhones) &&
                mCategory == that.mCategory &&
                Objects.equals(mUUID, that.mUUID) &&
                Objects.equals(mIsHeader, that.mIsHeader) &&
                Objects.equals(mURI, that.mURI) &&
                Objects.equals(mPhotoThumbnailURI, that.mPhotoThumbnailURI) &&
                Objects.equals(mIsPending, that.mIsPending) &&
                Objects.equals(mHasPhone, that.mHasPhone) &&
                Objects.equals(mEmail, that.mEmail) &&
                Objects.equals(mPhotoURI, that.mPhotoURI) &&
                Objects.equals(mAccountType, that.mAccountType) &&
                Objects.equals(mDirectoryName, that.mDirectoryName) &&
                Objects.equals(mAccountName, that.mAccountName) &&
                Objects.equals(mDirectoryID, that.mDirectoryID) && (mRefObject == that.mRefObject);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(mName, mFirstName, mLastName,
                mIsFavorite, mLocation, mCity, mCompany, mPosition,
                mPhones, mCategory, mUUID, mIsHeader, mURI, mPhotoThumbnailURI,
                mIsPending, mHasPhone, mEmail, mPhotoURI, mAccountType,
                mDirectoryName, mAccountName, mDirectoryID);
        result = 31 * result + Arrays.hashCode(mPhoto);
        return result;
    }

    /**
     * Setting up is {@link ContactData} favorite
     *
     * @param isFavorite boolean which represent is {@link ContactData} favorite
     */
    public void setIsFavorite(Boolean isFavorite) {
        this.mIsFavorite = isFavorite;
    }

    /**
     * set video call
     * @param context
     * @param listener
     */
    public void videoCall (Context context, OnContactInteractionListener listener) {
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        ContactData contactItem = setUpLocalContatData(context);
        if (contactItem != null) {
            listener.onCallContactVideo(contactItem, null);
        } else {
            listener.onCallContactVideo(this, null);
        }
    }

    /**
     * @param number Phone number of contact to be searched for
     * @return PhoneNumber|null
     */
    public PhoneNumber findPhoneNumber(String number) {
        if (mPhones != null) {
            for (PhoneNumber phoneNumber : mPhones) {
                if (phoneNumber.Number.replaceAll("[\\D]", "").startsWith(number)) {
                    return phoneNumber;
                }
            }
        }
        return null;
    }

    /**
     * @param firstNameFirst boolean
     * @return boolean
     */
    public String getFormatedName(boolean firstNameFirst) {
        String result = mName;
        if (!mFirstName.equals("") || !mLastName.equals("")) {
            if (!firstNameFirst) {
                result = mLastName + " " + mFirstName;
            } else {
                result = mFirstName + " " + mLastName;
            }
        }

        return result.trim();
    }

    /**
     * Set audio call
     * @param context
     * @param listener
     */
    public void audioCall (Context context, OnContactInteractionListener listener) {
        ContactData contactItem = setUpLocalContatData(context);
        if (contactItem != null) {
            listener.onCallContactAudio(contactItem, null);
        } else {
            listener.onCallContactAudio(this, null);
        }
    }

    /**
     * generate data needed for call
     * @param context
     * @return
     */
    private ContactData setUpLocalContatData(Context context) {
        if (this.mCategory == ContactData.Category.LOCAL || this.mCategory == Category.IPO) {
            List<ContactData.PhoneNumber> phoneNumbers = LocalContactInfo.getPhoneNumbers(Uri.parse(this.mURI), context);
            return new ContactData(this.mName, this.mFirstName, this.mFirstName, null, this.isFavorite(),
                    this.mLocation, this.mCity, this.mPosition, this.mCompany, phoneNumbers, this.mCategory,
                    this.mUUID, this.mURI, this.mPhotoThumbnailURI, this.mHasPhone, this.mEmail, this.mPhotoURI, this.mAccountType, "", "");
        }
        return null;
    }

    // enum used for phone types
    public enum PhoneType {
        WORK(0), MOBILE(1), HOME(2), HANDLE(3), FAX(4), PAGER(5), ASSISTANT(6), OTHER(7);
        private final int mValue;

        PhoneType(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }


    // Contact category types
    public enum Category {
        ALL("All Contacts", R.string.all_contacts),
        LOCAL("Local Contacts", R.string.local_contacts),
        ENTERPRISE("Enterprise Contacts", R.string.enterprise_contacts),
        PAIRED("Paired Contacts", R.string.local_contacts),
        DIRECTORY("Directory Contacts", R.string.personal_directory_contacts),
        IPO("IPO Contact", R.string.personal_directory_contacts),
        BROADSOFT("BroadSoft Contact", R.string.personal_directory_contacts);

        final String mName;
        final int mLabelId;

        Category(String name, final int labelId) {
            mName = name;
            mLabelId = labelId;
        }

        @Override
        public String toString() {
            return mName;
        }

        public int getLabelId() {
            return mLabelId;
        }

        static Category fromId(int i) {
            return Category.values()[i];
        }

        /**
         * Resolves {@link ContactProviderSourceType} into a {@link Category}
         *
         * @param sourceType {@link ContactProviderSourceType} to resolve
         * @return {@link Category} resolved from sourceType
         */
        public static Category fromContactSourceType(ContactProviderSourceType sourceType) {
            switch (sourceType){
                case LOCAL:
                    return Category.LOCAL;
                case IPO:
                    return Category.IPO;
                case PPM:
                case ACS:
                case ZANG:
                default:
                    return Category.ENTERPRISE;
            }
        }
    }

    public final String mName;
    public String mFirstName;
    public final String mLastName;
    public byte[] mPhoto;
    private Boolean mIsFavorite;
    public final String mLocation;
    public final String mCity;
    public final String mCompany;
    public final String mPosition;
    public List<PhoneNumber> mPhones;
    public final Category mCategory;
    public final String mUUID;
    private Boolean mIsHeader = false;
    public final String mURI;
    public final String mPhotoThumbnailURI;
    private Boolean mIsPending = false;
    public Boolean mHasPhone;
    public final String mEmail;
    public final String mPhotoURI;
    public String mAccountType;
    public String mDirectoryName;
    public String mAccountName;
    public String mDirectoryID;
    public Contact mRefObject = null;

    /**
     * An Object Representing phone number.
     */
    public static class PhoneNumber implements Parcelable {
        private static final int PHONE_TYPE = 1;
        public final String Number;
        public final PhoneType Type;
        public final boolean Primary;
        public final String phoneNumberId;

        /**
         * constructor
         * @param number phone number
         * @param type phone type
         * @param isPrimary is phone number primary
         * @param id phone number ID
         */
        public PhoneNumber(String number, PhoneType type, boolean isPrimary, String id) {
            Number = number;
            Type = type;
            Primary = isPrimary;
            phoneNumberId = id;
        }

        PhoneNumber(Parcel in) {
            Number = in.readString();
            Primary = in.readByte() != 0;
            Type = PhoneType.values()[in.readInt()];
            phoneNumberId = in.readString();
        }

        public static final Creator<PhoneNumber> CREATOR = new Creator<PhoneNumber>() {
            @Override
            public PhoneNumber createFromParcel(Parcel in) {
                return new PhoneNumber(in);
            }

            @Override
            public PhoneNumber[] newArray(int size) {
                return new PhoneNumber[size];
            }
        };

        /**
         * Describe the kinds of special objects contained in this Parcelable's
         * marshalled representation.
         *
         * @return a bitmask indicating the set of special object types marshalled
         * by the .
         */
        @Override
        public int describeContents() {
            return 0;
        }

        /**
         * Flatten this object in to a Parcel.
         *
         * @param dest  The Parcel in which the object should be written.
         * @param flags Additional flags about how the object should be written.
         *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
         */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(Number);
            dest.writeByte((byte) (Primary ? 1 : 0));
            dest.writeInt(Type.ordinal());
            dest.writeString(phoneNumberId);
        }
    }

    public ContactData(
            String name,
            String firstName,
            String lastName,
            byte[] photo,
            boolean favorite,
            String location,
            String city,
            String position,
            String company,
            List<PhoneNumber> phones,
            @NonNull Category category,
            @NonNull String uuid,
            String uri,
            String photoThumbnailURI,
            boolean hasPhone,
            String email,
            String photoUri,
            String accountType,
            String directoryName,
            String accountName) {

        this.mName = name;
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mPhoto = photo;
        this.setIsFavorite(favorite);
        this.mLocation = location;
        this.mCity = city;
        this.mPosition = position;
        this.mCompany = company;
        this.mPhones = phones;
        this.mCategory = category;
        this.mUUID = uuid;
        this.mURI = uri;
        this.mPhotoThumbnailURI = photoThumbnailURI;
        this.mHasPhone = hasPhone;
        this.mEmail = email;
        this.mPhotoURI = photoUri;
        this.mAccountType = accountType;
        this.mDirectoryName = directoryName;
        this.mAccountName = accountName;
    }

    /**
     * Generating dummy contact for given category
     * @param category contact category
     * @return contact data
     */
    public static ContactData getDummyContactForCategoryHeader(Category category) {
        ContactData data = new ContactData("Test Name", "First test name", "Last test name", null, false, "Test location", "Test city", "", "Avaya", null, category, "123456", null, null, true, "", "", "", "", "");
        data.mIsHeader = true;
        return data;
    }

    /**
     * Getting dummy contact for pending update
     * @param category contact category
     * @return contact data
     */
    public static ContactData getDummyContactForPendingUpdate(Category category) {
        ContactData data = new ContactData("", "", "", null, false, "", "", "", "Avaya", null, category, "", null, null, true, "", "","", "", "");
        data.mIsPending = true;
        return data;
    }

    /**
     * Getting empty contact data
     * @return empty contact data
     */
    public static ContactData getEmptyContactData() {
        ContactData data = new ContactData("", "", "", null, false, "", "", "", "", null, Category.ALL, "", null, null, true, "", "", "", "", "");
        data.mIsPending = true;
        return data;
    }

    @Override
    public String toString() {
        return mName;
    }

    public boolean isHeader() {
        return mIsHeader;
    }

    public void setIsHeader(boolean mIsHeader) {
        this.mIsHeader = mIsHeader;
    }

    public boolean isPendingUpdate() {
        return mIsPending;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    /**
     * Utility method to simplify copying of {@link ContactData} with these specific fields<br>
     * modified, as they are immutable
     *
     * @param photo byte[] of the photo
     * @param phoneNumbers list of {@link PhoneNumber}
     * @param accountType String account type
     * @param directoryName directory name
     * @param accountName account name
     * @return deep copy of this {@link ContactData} with parameter fields modified from original
     */
    public ContactData createNew(byte[] photo, List<PhoneNumber> phoneNumbers, String accountType, String directoryName, String accountName) {
        return new ContactData(
                mName,
                mFirstName,
                mLastName,
                photo,
                mIsFavorite,
                mLocation,
                mCity,
                mPosition,
                mCompany,
                phoneNumbers,
                mCategory,
                mUUID,
                mURI,
                mPhotoThumbnailURI,
                mHasPhone,
                mEmail,
                mPhotoURI,
                accountType,
                directoryName,
                accountName);
    }
}

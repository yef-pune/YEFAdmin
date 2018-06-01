package in.yefindia.yefadmin.activities;

public class AdminDetailsModel {

    public String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String contactNumber;
    public String state;
    public String city;
    public String email;
    public String age,bloodGroup,gender;

    public AdminDetailsModel(String fullName, String contactNumber, String state, String city, String email) {
        this.fullName = fullName;
        this.contactNumber = contactNumber;
        this.state = state;
        this.city = city;
        this.email = email;
    }
    public AdminDetailsModel(String fullName, String contactNumber, String email, String age, String bloodGroup, String gender) {
        this.fullName = fullName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.age = age;
        this.bloodGroup = bloodGroup;
        this.gender = gender;}
}

package ru.mail.polis;

import java.time.LocalDate;

/**
 * Created by Nechaev Mikhail
 * Since 13/12/2017.
 */
public class Student extends CheckedOpenHashTableEntity {

    private final double HASH_A = 0.6180339887;

    private static int counter = 0;

    //NotNullable поля
    private long id; //Уникальный идентификатор студента
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthday;
    private int groupId; //Идентификатор группы в которой учится студент
    private int yearOfAdmission; //Год поступления
    //Nullable поля
    private String photoReference; //Ссылка на фотографию студента
    private String email;
    private String mobile; //Номер телефона

    @Override
    public int hashCode(int tableSize, int probId) throws IllegalArgumentException {
        return Math.abs(h1(tableSize) + probId * h2(tableSize)) % tableSize;
    }

    private int h1(int tableSize) {
        return Math.abs(hashCode()) % tableSize;
    }

    private int h2(int tableSIze) {
        int hash = (int) (tableSIze * (HASH_A * Math.abs(hashCode29()) - (int) (HASH_A * Math.abs(hashCode29()))));
        return hash + (1 - (hash % 2));
    }

    public enum  Gender {
        MALE, FEMALE
    }

    public Student(String firstName, String lastName, Gender gender, LocalDate birthday, int groupId, int yearOfAdmission) {
        this.id = counter++;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
        this.groupId = groupId;
        this.yearOfAdmission = yearOfAdmission;
    }

    public Student(String firstName, String lastName, Gender gender, LocalDate birthday, int groupId,
                   int yearOfAdmission, String photoReference, String email, String mobile) {
        this(firstName, lastName, gender, birthday, groupId, yearOfAdmission);
        this.photoReference = photoReference;
        this.email = email;
        this.mobile = mobile;
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getYearOfAdmission() {
        return yearOfAdmission;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        if (id != student.id) return false;
        if (groupId != student.groupId) return false;
        if (yearOfAdmission != student.yearOfAdmission) return false;
        if (!firstName.equals(student.firstName)) return false;
        if (!lastName.equals(student.lastName)) return false;
        if (gender != student.gender) return false;
        if (!birthday.equals(student.birthday)) return false;
        if (photoReference != null ? !photoReference.equals(student.photoReference) : student.photoReference != null)
            return false;
        if (email != null ? !email.equals(student.email) : student.email != null) return false;
        return mobile != null ? mobile.equals(student.mobile) : student.mobile == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + gender.hashCode();
        result = 31 * result + birthday.hashCode();
        result = 31 * result + groupId;
        result = 31 * result + yearOfAdmission;
        result = 31 * result + (photoReference != null ? photoReference.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (mobile != null ? mobile.hashCode() : 0);
        return result;
    }

    public int hashCode29() {
        int result = (int) (id ^ (id >>> 32));
        result = 29 * result + firstName.hashCode();
        result = 29 * result + lastName.hashCode();
        result = 29 * result + gender.hashCode();
        result = 29 * result + birthday.hashCode();
        result = 29 * result + groupId;
        result = 29 * result + yearOfAdmission;
        result = 29 * result + (photoReference != null ? photoReference.hashCode() : 0);
        result = 29 * result + (email != null ? email.hashCode() : 0);
        result = 29 * result + (mobile != null ? mobile.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", birthday=" + birthday +
                ", groupId=" + groupId +
                ", yearOfAdmission=" + yearOfAdmission +
                ", photoReference='" + photoReference + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}

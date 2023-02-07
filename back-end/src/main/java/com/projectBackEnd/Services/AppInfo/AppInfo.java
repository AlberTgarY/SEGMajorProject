package main.java.com.projectBackEnd.Services.AppInfo;

/**
 * AppInfo object for storage of hospital names and department names.
 */
public class AppInfo {

    private String hospitalName;
    private String departmentName;
    private String contactDetails;
    /**
     * Default constructor
     */
    public AppInfo(){}

    /**
     * Create data storage objects
     * @param hospitalName      The name of the hospital
     * @param departmentName    The name of the department using the software
     * @param contactDetails    The contact information of the hospital
     */
    public AppInfo(String hospitalName, String departmentName, String contactDetails) {
        this.hospitalName = hospitalName;
        this.departmentName = departmentName;
        this.contactDetails = contactDetails;
    }

    /**
     * Get the hospital name as stored in the object
     * @return The hospital name
     */
    public String getHospitalName() {
        return hospitalName;
    }

    /**
     * Set the hospital name as stored in the object
     * @param hospitalName New hospital name
     */
    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    /**
     * Get the department name as stored in the object
     * @return The department name
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * Set the department name to be stored in the object
     * @param departmentName New department name
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    /**
     * Get the oontact details as stored in the object
     * @return The contact details string
     */
    public String getContactDetails() {
        return contactDetails;
    }

    /**
     * Set the contact details to be stored in the object
     * @param contactDetails The new contact details to update with
     */
    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }
}
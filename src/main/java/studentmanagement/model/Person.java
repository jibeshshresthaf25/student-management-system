package studentmanagement.model;

/**
 * Person is an ABSTRACT class. It cannot be instantiated on its own
 * (you can never write "new Person(...)").
 *
 * It exists so that any "person-like" entity in our system (right now
 * just Student, but it could be extended later, e.g. Instructor)
 * shares common fields (id, name, contact) and is forced to implement
 * displayInfo() and getRole() in its own way.
 *
 * This demonstrates: ABSTRACTION + the base for INHERITANCE.
 */
public abstract class Person {

    protected int id;
    protected String name;
    protected String contact;

    public Person(int id, String name, String contact) {
        this.id = id;
        this.name = name;
        this.contact = contact;
    }

    // ---- Encapsulation: private-ish (protected) fields, public getters/setters ----
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * Abstract method: every subclass MUST provide its own version of this.
     * This is what makes Person "abstract" and enables POLYMORPHISM -
     * different subclasses can print themselves differently, but we can
     * call displayInfo() on any Person reference and the correct version runs.
     */
    public abstract String displayInfo();

    /**
     * Another abstract method, just so subclasses identify their own role.
     */
    public abstract String getRole();
}

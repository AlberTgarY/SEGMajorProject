package main.java.com.projectBackEnd.Services.Site.Hibernate;

import main.java.com.projectBackEnd.TableEntity;
import org.hibernate.annotations.Type;

import javax.persistence.*;
/**
 * Site objects are database entities for the table 'Site' defined in this class.
 * They can be considered as a category of pages, one site is created for each condition.
 * The have an auto-increment primary key as 'ID' in table and a name as 'Name'.
 *
 *    https://examples.javacodegeeks.com/enterprise-java/hibernate/hibernate-annotations-example/
 */
@Entity
@Table(name = Site.TABLENAME)
public class Site implements TableEntity<Site> {

    // Table columns
    static final String TABLENAME = "Sites";
    public static final String ID = "ID";
    private static final String SITENAME = "Name";
    private static final String SITESLUG = "Slug";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Integer primaryKey;
	
    @Type(type="text")
    @Column(name = SITENAME, nullable=false)
    private String name;
	
    @Type(type="text")
    @Column(name = SITESLUG, nullable=false, unique=true)
    private String slug;

	/**
	* Default constructor
	*/
    public Site() {}

    /**
     * Main constructor
     * @param siteName  Name of the site
     * @param siteSlug  Slug of the site
     */
    public Site(String siteSlug, String siteName) {
        this.primaryKey = -1;
        this.name = siteName;
        setSlug(siteSlug);
    }

    /**
     * Constructor taking primary key 'ID'
     * @param ID        Primary key of the site
     * @param siteName  Name of the site
     * @param siteSlug  Slug of the site
     */
    public Site(Integer ID, String siteSlug, String siteName) {
        this.primaryKey = ID;
        this.name = siteName;
        setSlug(siteSlug);
    }

    /**
     * Get the primary key ID of the Site
     * @return primary key
     */
    @Override
    public Integer getPrimaryKey() {
        return primaryKey;
    }

    /**
     * Get the name of the Site
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the Site as the given name
     * @param name New name value
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Set the slug of the Site as the input slug
     * @param slug New slug value
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * Get the slug of the Site
     * @return slug
     */
    public String getSlug() { return slug; }
	
     /**
     * Copy the values of the input site object
     * @param siteToCopy The site object to be copied
     * @return updated site object
     */
    @Override
    public Site copy(Site siteToCopy) {

        setName(siteToCopy.getName());
        setSlug(siteToCopy.getSlug());
        return this;
    }


}

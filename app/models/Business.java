package models;

import java.util.ArrayList;

import javax.persistence.*;

import com.avaje.ebean.annotation.ConcurrencyMode;
import com.avaje.ebean.annotation.EntityConcurrencyMode;
import play.db.ebean.*;

@EntityConcurrencyMode(ConcurrencyMode.NONE)
@Entity
public class Business extends Model{

	/**
	 * Default Serial 
	 */
	private static final long serialVersionUID = 1L;


    @Id
	public String business_id;
	
	public String full_address;
	public String name;

    @ElementCollection
	public ArrayList<String> neighborhoods;
	public String city;
	public String state;
	public double latitude;
	public double longitude;
	public double stars;
	public int review_count;

    @Transient
	public ArrayList<Category> categories;
    private String categoriesDB;

    @OneToMany(mappedBy = "business")
    public ArrayList<Tip> tips;

    @Transient
    public ArrayList<AttributeDB> attributes;
    private String attributesDB;

	public boolean open;//TODO??


    /**
	 * Modela los tiempos de apertura para cada uno de los 7 dias de la semana
	 * en las filas se encuentran los dias: 0-Domingo, 6-sabado 
	 * La primera columna es la hora de apertura en hora militar (0-2359), -1 si no abre ese dia
	 * La segunda columna es la hora de cierre en hora militar (0-2359), -1 si no abre ese dia
	 */
    @Transient
	public ArrayList<ArrayList<Integer>> openTimes;

    private String opentimesString;
	
	/**
	 * @return the openTimes
	 */

	public ArrayList<ArrayList<Integer>> getOpenTimes() {
        if(openTimes==null || openTimes.size()==0)
        {
            openTimes=new ArrayList<ArrayList<Integer>>();
            if(!opentimesString.isEmpty())
            {
                String[] pares=opentimesString.split(";");
                for (int i = 0; i <pares.length; i++) {
                    ArrayList<Integer> par=new ArrayList<>();
                    String[] p1=pares[i].split(",");
                    par.add(Integer.parseInt(p1[0]));
                    par.add(Integer.parseInt(p1[1]));
                    openTimes.add(par);
                }
            }

        }
		return openTimes;
	}
	/**
	 * Estructura de datos para el modelo de atributos
	 * en la primera casilla se ubica el id del atributo
	 * La segunda casilla el valor true (1) o false(0) del atributo
	 */

    public static Finder<String,Business> find = new Finder<String,Business>(
            String.class, Business.class
    );

	public String getBusiness_id() {
		return business_id;
	}

	public void setBusiness_id(String business_id) {
		this.business_id = business_id;
	}

	public String getFull_address() {
		return full_address;
	}

	public void setFull_address(String full_address) {
		this.full_address = full_address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getNeighborhoods() {
		return neighborhoods;
	}

	public void setNeighborhoods(ArrayList<String> neighborhoods) {
		this.neighborhoods = neighborhoods;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getStars() {
		return stars;
	}

	public void setStars(double stars) {
		this.stars = stars;
	}

	public int getReview_count() {
		return review_count;
	}

	public void setReview_count(int review_count) {
		this.review_count = review_count;
	}

	public ArrayList<Category> getCategories() {
        if(categories==null||categories.isEmpty())
        {
            categories=new ArrayList<>();
            if(categoriesDB==null||categories.isEmpty())
                return categories;
            String[] catids=categoriesDB.split(",");
            for( String id:catids)
            {
                Category f=Category.find.byId(Long.parseLong(id));
                if(f!=null)
                    categories.add(f);
            }
        }
		return categories;
	}

	public void setCategories(ArrayList<Category> categories) {
		this.categories = categories;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public ArrayList<AttributeDB> getAttributes() {
        if(attributes==null||attributes.isEmpty())
        {
            attributes=new ArrayList<>();
            if(attributesDB==null||attributesDB.isEmpty())
                return attributes;
            String[] atids=attributesDB.split(",");
            for( String id:atids)
            {
                AttributeDB f=AttributeDB.find.byId(Long.parseLong(id));
                if(f!=null)
                    attributes.add(f);
            }
        }
		return attributes;
	}

	public void setAttributes(ArrayList<AttributeDB> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @param openTimes the openTimes to set
	 */
	public void setOpenTimes(ArrayList<ArrayList<Integer>> openTimes) {
		this.openTimes = openTimes;
	}

    @Override
    public void save() {


        if((attributesDB==null||attributesDB.isEmpty())&&attributes!=null)
        {
            attributesDB="";
            for( AttributeDB at:attributes)
            {
                attributesDB+=","+at.getID();
            }
            attributesDB=attributesDB.isEmpty()?"":attributesDB.substring(1);
        }
        else if(attributesDB!=null&&!attributesDB.isEmpty())
        {
            String newatr="";
            try {


                String[] vals = attributesDB.split(",");
                for (String v:vals)
                {
                    AttributeDB atdb=AttributeDB.find.where().eq("name",v).findList().get(0);
                    if(atdb!=null)
                        newatr+=","+atdb.getID();
                }
                attributesDB=newatr.isEmpty()?"":newatr.substring(1);
            }
            catch (Exception e)
            {

            }
        }
        opentimesString="";
        for(ArrayList<Integer> par:getOpenTimes())
        {
            opentimesString+=";"+par.get(0)+","+par.get(0);
        }
        opentimesString=opentimesString.isEmpty()?"":opentimesString.substring(1);

        categoriesDB="";
        for( Category at:getCategories())
        {
            categoriesDB+=","+at.getID();
        }
        String assigned=categoriesDB.isEmpty()?"":categoriesDB.substring(1);
        categoriesDB=assigned;

        super.save();
    }

    public void setAttributesString(String attributesString) {
        this.attributesDB = attributesString;
    }
}

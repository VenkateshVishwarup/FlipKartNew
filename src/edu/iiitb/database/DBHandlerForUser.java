	package edu.iiitb.database;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import edu.iiitb.model.Advertizement;
import edu.iiitb.model.CartProduct;
import edu.iiitb.model.CategoryModel;
import edu.iiitb.model.CompareCartProduct;
import edu.iiitb.model.FeedbackModel;
import edu.iiitb.model.Linklists;
import edu.iiitb.model.ProductInfo;
import edu.iiitb.model.SignupModel;
import edu.iiitb.model.UserEntry;
import edu.iiitb.model.customerCartDetail;
import edu.iiitb.model.custometAddressDetail;
import edu.iiitb.model.getKeywords;
import edu.iiitb.model.getUserBankDetails;


	/**
	 * @author paras
	 *
	 */
public class DBHandlerForUser {

	/**
	 * 
	 */
	DBConnectivity db=new DBConnectivity();
	
	
	public boolean chkForEmailIDAlreadyExists(String email) throws SQLException
	{
		Connection con = db.createConnection();
		String query="select email from UserCredantials";
		ResultSet rs=db.executeQuery(query, con);
		while(rs.next())
		{
			if(rs.getString("email").equals(email))
			{
				con.close();
				return true;
			}
		}
		return false;
	}
	
	public boolean SignupUserinDB(SignupModel user) throws SQLException
	{	
		Connection con = db.createConnection();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String date = sdf.format(new java.util.Date());
		String[] splitedDate=user.getDate().split("T");
		String query="INSERT INTO UserCredantials(`firstName`,`lastName`,`password`,`role`,`dateOfBirth`,`addressLine1`,`addressLine2`,`city`,`country`,`pinCode`,`email`,`phoneNumber`,`dateOfRegistration`,`gender`) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement prep =con.prepareStatement(query);
		
		prep.setString(1, user.getFirstName());
		prep.setString(2, user.getLastName());
		prep.setString(3, user.getPassword());
		prep.setString(4, "User");
		prep.setString(5, splitedDate[0]);
		prep.setString(6, user.getAddress1());
		prep.setString(7, user.getAddress2());
		prep.setString(8, user.getCity());
		prep.setString(9, user.getCountry());
		prep.setString(10, user.getPincode());
		prep.setString(11, user.getEmail());
		prep.setString(12, user.getPhonenumber());
		prep.setString(13, date );
		prep.setString(14,user.getGender());
		prep.execute();
		
		con.close();
		return true;
	}

	public String chkForEmailID_PasswordAlreadyExists(String email,
			String password) throws SQLException 
	{
		Connection con = db.createConnection();
		String role=null;
		String query="select email,password,role from UserCredantials";
		ResultSet rs=db.executeQuery(query, con);
		while(rs.next())
		{
			if(rs.getString("email").equals(email)&&rs.getString("password").equals(password))
				role=rs.getString("role");
		}
		if (role==null)
		{
			return "invalid";
		}
		con.close();
		return role;
			
	}

	
	public String chkForEmailID(String email) throws SQLException 
	{
		Connection con = db.createConnection();
		String role=null;
		String query="select email,role from UserCredantials";
		ResultSet rs=db.executeQuery(query, con);
		while(rs.next())
		{
			if(rs.getString("email").equals(email))
				role=rs.getString("role");
		}
		if (role==null)
		{
			return "invalid";
		}
		con.close();
		return role;
			
	}

	
	public String getPasswordformDB(String email) throws SQLException 
	{
		// TODO Auto-generated method stub
		Connection con = db.createConnection();
		String query="select email,password from UserCredantials";
		ResultSet rs=db.executeQuery(query, con);
		String password=null;
		while(rs.next())
		{
			if(rs.getString("email").equals(email))
			{
				password=rs.getString("password");
		
			}
		}
		con.close();
		return password;
	
	}	
	
	public ArrayList<Advertizement> getadvertizement(String Type, String Limit) throws SQLException, IOException
	{
		Connection con = db.createConnection();
		ArrayList<Advertizement> advertize = new ArrayList<Advertizement>();
		DBConnectivity db=new DBConnectivity();															
		
		String query="SELECT a.productId, a.timeStamp, a.image, a.caption " +
				" FROM Advertizement as a " +
				" where a.advertizementType= '" +Type +"' " +
						" ORDER BY a.timeStamp desc LIMIT " + Limit;
	
		ResultSet rs=db.executeQuery(query, con);
		
		while(rs.next())
		{
			Advertizement obj = new Advertizement();
			obj.setProductId(rs.getInt("productId"));
			obj.setTimeStamp(rs.getTimestamp("timeStamp"));
			obj.setPhoto(rs.getString("image"));
			obj.setCaption(rs.getString("caption"));
			
			advertize.add(obj);
			
		}
		db.closeConnection(con);
		return advertize;
	}
	public ArrayList<getKeywords> getKeywordList(String keywordToCheck) throws SQLException, IOException
	{
		ArrayList<getKeywords> KeywordList = new ArrayList<getKeywords>();
		DBConnectivity db=new DBConnectivity();	

		String query="Select distinct(p.productId), p.image, p.productName " +
				" from ProductInfo as p, (select distinct(k.keyword), k.productId " +
											" from Keywords as k " +
											" where k.keyword like '%"+keywordToCheck+"%') as keyw " +
				" where p.productId = keyw.productId ORDER BY p.productId desc LIMIT 5";
		Connection con= db.createConnection();		
		ResultSet rs=db.executeQuery(query, con);
		while(rs.next())
		{
			getKeywords obj = new getKeywords();
			obj.setProductId(rs.getString("productId"));
			obj.setProductImage(rs.getString("image"));
			obj.setProductName(rs.getString("productName"));
			KeywordList.add(obj);
		}
		return KeywordList;
	}
	
	public ArrayList<Advertizement> getadvertizementforfront(String Type, ArrayList<String> categorylist) throws SQLException, IOException
	{
		Connection con = db.createConnection();
		ArrayList<Advertizement> advertize = new ArrayList<Advertizement>();
		DBConnectivity db=new DBConnectivity();															
		String query="";
		query += "SELECT a.productId, a.image, a.timeStamp from (";
		for(int i=0; i<categorylist.size(); i++)
		{
			query += " SELECT distinct(adv.productId), adv.image, adv.timeStamp " +
					" FROM Advertizement as adv, ProductInfo as p" +
					" where adv.advertizementType= '"+Type+"' " +
					" and adv.productId = p.productId " +
					" and p.categoryId = '"+categorylist.get(i)+"' ";            
			if(i<(categorylist.size()-1))
				query += " union ";
		}
		query += " ) as a ORDER BY a.timeStamp desc LIMIT 3 ";
		ResultSet rs=db.executeQuery(query, con);
		
		while(rs.next())
		{
			Advertizement obj = new Advertizement();
			obj.setProductId(rs.getInt("productId"));
			obj.setPhoto(rs.getString("image"));
			obj.setTimeStamp(rs.getTimestamp("timeStamp"));
			advertize.add(obj);
		}
		db.closeConnection(con);
		return advertize;
	}
	
	public void getsubcategorylist(int parentcategoryId,  ArrayList<CategoryModel> categoryModel) throws SQLException, IOException
	{
		DBConnectivity db=new DBConnectivity();
		Connection con= db.createConnection();																
		
		String query= " SELECT Category.categoryName, Category.categoryId " +
				" FROM Category, CategoryRelation " +
				" WHERE Category.categoryId = CategoryRelation.subCategoryId " +
				" AND CategoryRelation.categoryId =" + parentcategoryId;  
	
		ResultSet rs=db.executeQuery(query, con);
		
		while(rs.next())
		{
			CategoryModel obj = new CategoryModel();
			obj.setCategoryName(rs.getString("categoryName"));
			obj.setCategoryId(rs.getString("categoryId"));
			categoryModel.add(obj);
		}
		db.closeConnection(con);
	}
	
	public ArrayList<CategoryModel> getsubcategorylistancestor(String parentcategoryId, String ancestorname) throws SQLException, IOException
	{
		ArrayList<CategoryModel> categoryModel = new ArrayList<CategoryModel>();
		DBConnectivity db=new DBConnectivity();
		Connection con= db.createConnection();	
		
		String query= " SELECT Category.categoryName, Category.categoryId " +
				" FROM Category, CategoryRelation " +
				" WHERE Category.categoryId = CategoryRelation.subCategoryId " +
				" AND CategoryRelation.categoryId = '"+ parentcategoryId +"' " +
						" and Category.categoryName Like '"+ ancestorname+"%'";       
	
		ResultSet rs=db.executeQuery(query, con);
		
		while(rs.next())
		{
			CategoryModel obj = new CategoryModel();
			obj.setCategoryName(rs.getString("categoryName"));
			obj.setCategoryId(rs.getString("categoryId"));
			categoryModel.add(obj);
		}
		db.closeConnection(con);
		return categoryModel;
	}
	
	public ArrayList<CategoryModel> getsubsubcategorylist(String parentcategoryid) throws SQLException, IOException
	{
		ArrayList<CategoryModel> categoryModel = new ArrayList<CategoryModel>();
		DBConnectivity db=new DBConnectivity();
		Connection con= db.createConnection();																
		String query= " SELECT c2.categoryName as subcategory, c2.categoryId as subcategoryid " +
				" FROM Category as c1, Category as c2, CategoryRelation " +
				" WHERE c2.categoryId = CategoryRelation.subCategoryId " +
				" AND c1.categoryId = '" + parentcategoryid + "' " +
						" AND c1.categoryId = CategoryRelation.categoryId";  
	
		ResultSet rs=db.executeQuery(query, con);
		
		while(rs.next())
		{
			CategoryModel obj = new CategoryModel();
			obj.setCategoryName(rs.getString("subcategory"));
			obj.setCategoryId(rs.getString("subcategoryid"));
			categoryModel.add(obj);
		}
		db.closeConnection(con);
		return categoryModel;
	}
	
	public ArrayList<CategoryModel> getsubcategorydeatils(int parentcategoryId) throws SQLException, IOException
	{
		ArrayList<CategoryModel> categoryModel = new ArrayList<CategoryModel>();
		DBConnectivity db=new DBConnectivity();
		Connection con= db.createConnection();																
		
		String query= " SELECT Category.categoryName, Category.categoryId, Category.image " +
				" FROM Category, CategoryRelation " +
				" WHERE Category.categoryId = CategoryRelation.subCategoryId " +
				" AND CategoryRelation.categoryId =" + parentcategoryId;  
	
		ResultSet rs=db.executeQuery(query, con);
		
		while(rs.next())
		{
			CategoryModel obj = new CategoryModel();
			obj.setCategoryName(rs.getString("categoryName"));
			obj.setCategoryId(rs.getString("categoryId"));
			obj.setCategoryImage(rs.getString("image"));
			categoryModel.add(obj);
		}
		return categoryModel;
	}
		
	
		public String getfName(String email) throws SQLException
		{
			// TODO Auto-generated method stub
			Connection con = db.createConnection();
			String query="select email,firstName from UserCredantials";
			ResultSet rs=db.executeQuery(query, con);
			String fName=null;
			while(rs.next())
			{
				if(rs.getString("email").equals(email))
				{
					fName=rs.getString("firstName");
			
				}
			}
			con.close();
			return fName;
		
		}
	public String chkUserId(String email) throws SQLException
	{
		// TODO Auto-generated method stub
		Connection con = db.createConnection();
		String query="select email,userId from UserCredantials";
		ResultSet rs=db.executeQuery(query, con);
		String userId=null;
		while(rs.next())
		{
			if(rs.getString("email").equals(email))
			{
				userId=rs.getString("userId");
		
			}
		}
		con.close();
		return userId;
	}	
	
	public UserEntry getpersonalinfo(String email) throws SQLException
	{
		// TODO Auto-generated method stub
		DBConnectivity db=new DBConnectivity();
		Connection con=db.createConnection();
		String query="select * from UserCredantials";
		ResultSet rs=db.executeQuery(query, con);
		UserEntry user = new UserEntry();
		while(rs.next())
		{
			if(rs.getString("email").equals(email))
			{
		
		user.setFirstName(rs.getString("firstName"));
		user.setLastName(rs.getString("lastName"));
		user.setGender(rs.getString("gender"));
		user.setPhonenumber(rs.getString("phoneNumber"));
		user.setCity(rs.getString("city"));
		user.setPinCode(rs.getInt("pinCode"));
		user.setAddress1(rs.getString("addressLine1"));
		user.setAddress2(rs.getString("addressLine2"));
			}
	}
		return user;
	}
	
	public void updatepersonalinfo(UserEntry user) throws SQLException
	{
		DBConnectivity db=new DBConnectivity();
		Connection con=db.createConnection();

		String query1="UPDATE UserCredantials SET firstName =?,lastName =?, gender =?, phoneNumber =? WHERE email=?";
		PreparedStatement prep1 =con.prepareStatement(query1);
		prep1.setString(1, user.getFirstName());
		prep1.setString(2, user.getLastName());
		prep1.setString(3, user.getGender());
		prep1.setString(4, user.getPhonenumber());
		prep1.setString(5, user.getEmail());
		
		prep1.execute();
		con.close();
	}
	
	public void updatepassword(UserEntry user) throws SQLException
	{
		DBConnectivity db=new DBConnectivity();
		Connection con=db.createConnection();

		String query1="UPDATE UserCredantials SET password =? WHERE email=?";
		PreparedStatement prep1 =con.prepareStatement(query1);
		prep1.setString(1, user.getPassword());
		prep1.setString(2, user.getEmail());
		
		prep1.execute();
		con.close();
	}
	
	public void updateaddress(UserEntry user) throws SQLException
	{
		DBConnectivity db=new DBConnectivity();
		Connection con=db.createConnection();

		String query1="UPDATE UserCredantials SET firstName =?,lastName =?,phoneNumber =?,addressLine1 =?,addressLine2 =?,pinCode =?,city =? WHERE email=?";
		PreparedStatement prep1 =con.prepareStatement(query1);
		prep1.setString(1, user.getFirstName());
		prep1.setString(2, user.getLastName());
		prep1.setString(3, user.getPhonenumber());
		prep1.setString(4, user.getAddress1());
		prep1.setString(5, user.getAddress2());
		prep1.setInt(6, user.getPinCode());
		prep1.setString(7, user.getCity());
		prep1.setString(8, user.getEmail());
		
		prep1.execute();
		con.close();
	}
	
	public void updateemail(UserEntry user) throws SQLException
	{
		DBConnectivity db=new DBConnectivity();
		Connection con=db.createConnection();

		String query1="UPDATE UserCredantials SET email =? WHERE email=?";
		PreparedStatement prep1 =con.prepareStatement(query1);
		prep1.setString(1, user.getNewemail());
		prep1.setString(2, user.getEmail());
		
		prep1.execute();
		con.close();
	}
	

	
	public ArrayList<ProductInfo> getproductinfo(int Productid) throws SQLException
	{
		Connection con = db.createConnection();
		ArrayList<ProductInfo> ProductInfo = new ArrayList<ProductInfo>();	
		String query=" Select p.productId, p.productName, p.price, p.image, p.offer, p.categoryId, p.description, p.brand, p.warranty, p.offerValidity, s.sellerId, " +
				" u.firstName, u.lastName " +
				" from ProductInfo as p, Seller as s, Stock as st, UserCredantials as u " +
				" where p.productId = '" + Productid + "' " +
						" and p.productId = st.productId " +
						" and st.sellerId = s.sellerId " +
						" and s.userId = u.userId ";   
		ResultSet rs=db.executeQuery(query, con);
		while(rs.next())
		{
			ProductInfo obj = new ProductInfo();
			obj.setProductID(rs.getInt("productId"));
			obj.setProductName(rs.getString("productName"));
			obj.setPrice(rs.getInt("price"));
			obj.setImage(rs.getString("image"));
			obj.setOffer(rs.getInt("offer"));
			obj.setCategoryID(rs.getString("categoryId"));
			obj.setDescription(rs.getString("description"));
			obj.setBrand(rs.getString("brand"));
			obj.setWarranty(rs.getInt("warranty"));
			obj.setSellerID(rs.getString("sellerId"));
			obj.setSellerFName(rs.getString("firstName"));
			obj.setSellerLName(rs.getString("lastName"));
			obj.setOfferValidity(rs.getTimestamp("offerValidity"));
			ProductInfo.add(obj);
		}
		db.closeConnection(con);
		return ProductInfo;
	}
	
	
	public ArrayList<ProductInfo> getproductlistoncategoryfilter(String[] brand, String[] price, ArrayList<String> categoryid, int count, int countprice) throws SQLException
	{
		Connection con = db.createConnection();
		ArrayList<ProductInfo> ProductInfo = new ArrayList<ProductInfo>();	
		String temp="", query="";
		ResultSet rs;
		if(count > 0 && countprice >0)
		{
			for(int i=0; i<count; i++)
			{
				for(int j=0; j<categoryid.size(); j++)
				{	
					for(int k=0; k<countprice; k++)
					{
						query += "Select p.productId, p.productName, p.price, p.image, p.offer, p.categoryId, p.description, p.brand, p.warranty, p.offerValidity, " +
								" s.availableQuantity, s.minimumQuantity" +
								" from ProductInfo as p, Category as c , Stock as s " +
								" where p.categoryId = c.categoryId " +
								" and  p.productId = s.productId " +
								" and c.categoryId = '" + categoryid.get(j) + "' " +
										" and p.brand = '" + brand[i] + "' ";
		
						if(price[k].equalsIgnoreCase("1"))
						{
						query +=" and (p.price-p.offer) between 0 and 2000"; 
						}
						else if(price[k].equalsIgnoreCase("2"))
						{
						query += " and (p.price-p.offer) between 2001 and 5000";       
						}
						else if(price[k].equalsIgnoreCase("3"))
						{
						query += " and (p.price-p.offer) between 5001 and 10000";     
						}
						else if(price[k].equalsIgnoreCase("4"))
						{
						query += " and (p.price-p.offer) between 10001 and 18000";      
						}
						else if(price[k].equalsIgnoreCase("5"))
						{
						query += " and (p.price-p.offer) between 18001 and 25000";      
						}
						else if(price[k].equalsIgnoreCase("6"))
						{
						query += " and (p.price-p.offer) between 25001 and 35000";       
						}
						else if(price[k].equalsIgnoreCase("7"))
						{
						query += " and (p.price-p.offer) > 35001";      
						}
						if(i == (count-1) && j == (categoryid.size()-1) && k == (countprice-1))
						{
						}
						else
						{
							query +=" UNION ";
						}
					}
				}
			}
		}
		else if(count > 0)
		{
			for(int i=0; i<count; i++)
			{
				for(int j=0; j<categoryid.size(); j++)
				{
					query += "Select p.productId, p.productName, p.price, p.image, p.offer, p.categoryId, p.description, p.brand, p.warranty, p.offerValidity, " +
							" s.availableQuantity, s.minimumQuantity " +
							" from ProductInfo as p, Category as c, Stock as s " +
							" where p.categoryId = c.categoryId " +
							" and  p.productId = s.productId " +
							" and c.categoryId = '" + categoryid.get(j) + "'" +
									" and p.brand = '" + brand[i] + "'"; 
					if(i == (count-1) && j == (categoryid.size()-1))
					{
					}
					else
					{
						query +=" UNION ";
					}
				}
			}
		}
		else if(countprice > 0)
		{
			for(int i=0; i<countprice; i++)
			{
				for(int j=0; j<categoryid.size(); j++)
				{
					query += "Select distinct(p.productId), p.productName, p.price, p.image, p.offer, p.categoryId, p.description, p.brand, p.warranty, p.offerValidity, " +
							" s.availableQuantity, s.minimumQuantity " +
							" from ProductInfo as p, Stock as s " +
							" where p.categoryId = '" + categoryid.get(j) + "' " +
									" and  p.productId = s.productId";
					if(price[i].equalsIgnoreCase("1"))
					{
						query += " and (p.price-p.offer) BETWEEN 0 AND 2000";
					}
					else if(price[i].equalsIgnoreCase("2"))
					{
						query += " and (p.price-p.offer) BETWEEN 2001 AND 5000";
					}
					else if(price[i].equalsIgnoreCase("3"))
					{
						query += " and (p.price-p.offer) BETWEEN 5001 AND 10000";
					}
					else if(price[i].equalsIgnoreCase("4"))
					{
						query += " and (p.price-p.offer) BETWEEN 10001 AND 18000";
					}
					else if(price[i].equalsIgnoreCase("5"))
					{
						query += " and (p.price-p.offer) BETWEEN 18000 AND 25000";
					}
					else if(price[i].equalsIgnoreCase("6"))
					{
						query += " and (p.price-p.offer) BETWEEN 25001 AND 35000";       
					}
					else if(price[i].equalsIgnoreCase("7"))
					{
						query += " and (p.price-p.offer) > 35000";
					}
					
					if(i == (countprice-1) && j == (categoryid.size()-1))
					{
					}
					else
					{
						query +=" UNION ";
					}
				}
			}
		}
		rs=db.executeQuery(query, con);
		while(rs.next())
		{
			ProductInfo obj = new ProductInfo();
			obj.setProductID(rs.getInt("productId"));
			obj.setProductName(rs.getString("productName"));
			obj.setPrice(rs.getInt("price"));
			obj.setImage(rs.getString("image"));
			obj.setOffer(rs.getInt("offer"));
			obj.setCategoryID(rs.getString("categoryId"));
			obj.setDescription(rs.getString("description"));
			obj.setBrand(rs.getString("brand"));
			obj.setWarranty(rs.getInt("warranty"));
			obj.setMinimumQuantity(rs.getInt("minimumQuantity"));
			obj.setAvailableQuantity(rs.getInt("availableQuantity"));
			obj.setOfferValidity(rs.getTimestamp("offerValidity"));
			ProductInfo.add(obj);
		}
		db.closeConnection(con);
		return ProductInfo;
	}
		
	
	public ArrayList<Linklists> getlinktothecategory(String category) throws SQLException
	{
		ArrayList<Linklists> Listoflink = new ArrayList<Linklists>();
		Connection con = db.createConnection();
		String query="Select distinct(c2.categoryName) as parentCategory, c1.categoryName as childCategory " +
				" from Category as c1, Category as c2, CategoryRelation " +
				" where c2.categoryName = '"+category+"' " +
						" and c2.categoryId = CategoryRelation.subCategoryId " +
						" and CategoryRelation.categoryId = c1.categoryId";       
		ResultSet rs=db.executeQuery(query, con);
		if(rs.next() == false)
		{
			query="Select distinct(c1.categoryName) as ParentCategory, c2.categoryName as childCategory " +
					" from Category as c1, Category as c2, CategoryRelation " +
					" where c1.categoryName = '"+category+"' " +
							" and c1.categoryId = CategoryRelation.categoryId " +
							" and CategoryRelation.subCategoryId = c2.categoryId";
			rs=db.executeQuery(query, con);
			while(rs.next())
			{
				// This is the case when their is no parent category and we need only child category
				Linklists obj = new Linklists();
				obj.setParentCategory(rs.getString("parentCategory"));
				obj.setCategory(rs.getString("childCategory"));
				Listoflink.add(obj);
			}
		}
		else
		{
			rs.previous();
			if(rs.next())
			{
				// This is the case of child category
				Linklists obj = new Linklists();
				obj.setParentCategory(rs.getString("childCategory"));
				obj.setCategory(rs.getString("parentCategory"));
				Listoflink.add(obj);
			}
		}
		
		
		db.closeConnection(con);
		return Listoflink;
	}
	
	public String getnameonid(String id) throws SQLException
	{
		Connection con = db.createConnection();
		String name="";
		String query="Select categoryName from Category where categoryId = '"+id+"'";
		ResultSet rs=db.executeQuery(query, con);
		while(rs.next())
		{
			name = rs.getString("categoryName");
		}
		con.close();
		return name;
	}
	
	public String getidonname(String name) throws SQLException
	{
		Connection con = db.createConnection();
		String id="";
		String query="Select categoryId from Category where  categoryName = '"+name+"'";
		ResultSet rs=db.executeQuery(query, con);
		while(rs.next())
		{
			id = rs.getString("categoryId");
		}
		con.close();
		return id;
	}
	
	public ArrayList<String> getCategoryList(String categoryid) throws SQLException
	{
		ArrayList<String> categoryList = new ArrayList<String>();
		Connection con = db.createConnection();
		String query="Select CategoryRelation.subCategoryId " +
				" from Category, CategoryRelation " +
				" where Category.categoryId = '"+ categoryid +"'" +
						" and Category.categoryId = CategoryRelation.categoryId";     
		ResultSet rs=db.executeQuery(query, con);
		while(rs.next())
		{
			categoryList.add(rs.getString("subCategoryId"));
		}
		con.close();
		return categoryList;
	}
	
	public ArrayList<String> getCategoryListwithcategory(String categoryid, String Parentcategory) throws SQLException
	{
		ArrayList<String> categoryList = new ArrayList<String>();
		Connection con = db.createConnection();
		String query="Select CategoryRelation.subCategoryId " +
				" from Category as c1, Category as c2, CategoryRelation " +
				" where c1.categoryId = '"+ categoryid +"' " +
						" and c1.categoryId = CategoryRelation.categoryId " +
						" and c2.categoryId = CategoryRelation.subCategoryId " +
						" and c2.categoryName Like '"+ Parentcategory+"%'";     

		ResultSet rs=db.executeQuery(query, con);
		while(rs.next())
		{
			categoryList.add(rs.getString("subCategoryId"));
		}
		con.close();
		return categoryList;
	}
	
	public ArrayList<ProductInfo> getproductlistoncategory(ArrayList<String> category) throws SQLException
	{
		Connection con = db.createConnection();
		ArrayList<ProductInfo> ProductInfo = new ArrayList<ProductInfo>();	
		String query = "";
		
		for(int i=0; i<category.size(); i++)
		{
			String categoryid = category.get(i);
			query += "select p.productId, p.productName, p.price, p.image, p.offer, p.categoryId, p.description, p.brand, p.warranty, p.offerValidity, s.availableQuantity, s.minimumQuantity " +
					" from ProductInfo as p , Category as c, Stock as s " +
					" where p.categoryId = c.categoryId " +
					" and  p.productId = s.productId " +
					" and c.categoryId = '" + categoryid + "'";    
			if(i<(category.size()-1))
				query += " Union ";
		}
		
		ResultSet rs=db.executeQuery(query, con);
			
		while(rs.next())
		{	
			ProductInfo obj = new ProductInfo();
			obj.setProductID(rs.getInt("productId"));
			obj.setProductName(rs.getString("productName"));
			obj.setPrice(rs.getInt("price"));
			obj.setImage(rs.getString("image"));
			obj.setOffer(rs.getInt("offer"));
			obj.setCategoryID(rs.getString("categoryId"));
			obj.setDescription(rs.getString("description"));
			obj.setBrand(rs.getString("brand"));
			obj.setWarranty(rs.getInt("warranty"));
			obj.setMinimumQuantity(rs.getInt("minimumQuantity"));
			obj.setAvailableQuantity(rs.getInt("availableQuantity"));
			obj.setOfferValidity(rs.getTimestamp("offerValidity"));
			ProductInfo.add(obj);
		}
	
		
		db.closeConnection(con);
		return ProductInfo;
		
	}
	
	public ArrayList<String> getCompanylistoncategory(String category) throws SQLException
	{
		Connection con = db.createConnection();
		ArrayList<String> companyname = new ArrayList<String>();	
		String query="Select distinct(p.brand) " +
				" from ProductInfo as p, Category as c " +
				" where c.categoryId = p.categoryId " +
				" and c.categoryName = '" + category + "'";       
		ResultSet rs=db.executeQuery(query, con);
		
		while(rs.next())
		{
			companyname.add(rs.getString("brand"));
		}
		db.closeConnection(con);
		return companyname;
	}
	
	public ArrayList<String> getCompanylistoncategory(ArrayList<String> category) throws SQLException
	{
		Connection con = db.createConnection();
		ArrayList<String> companyname = new ArrayList<String>();	
		String query="";
		for(int i=0; i<category.size(); i++)
		{
			query += "Select distinct(ProductInfo.brand) " +
					" from ProductInfo where ProductInfo.categoryId = '" + category.get(i) + "'";       
			if(i<(category.size()-1))
				query += " union ";
		}
		ResultSet rs=db.executeQuery(query, con);
		
		while(rs.next())
		{
			companyname.add(rs.getString("brand"));
		}
		db.closeConnection(con);
		return companyname;
	}
	
	public ArrayList<ProductInfo> getproductlist(String keyword) throws SQLException
	{
		Connection con = db.createConnection();
		ArrayList<ProductInfo> ProductInfo = new ArrayList<ProductInfo>();	
		String query="Select p.productId, p.productName, p.price, p.image, p.offer, p.categoryId, p.description, p.brand, p.warranty, p.offerValidity, s.availableQuantity, s.minimumQuantity " +
				" from ProductInfo as p, Keywords as k, Stock as s" +
				" where p.productId = k.productId " +
				" and  p.productId = s.productId " +
				" and k.keyword = '" + keyword + "'";       
		ResultSet rs=db.executeQuery(query, con);
		
		while(rs.next())
		{	
			ProductInfo obj = new ProductInfo();
			obj.setProductID(rs.getInt("productId"));
			obj.setProductName(rs.getString("productName"));
			obj.setPrice(rs.getInt("price"));
			obj.setImage(rs.getString("image"));
			obj.setOffer(rs.getInt("offer"));
			obj.setCategoryID(rs.getString("categoryId"));
			obj.setDescription(rs.getString("description"));
			obj.setBrand(rs.getString("brand"));
			obj.setWarranty(rs.getInt("warranty"));
			obj.setOfferValidity(rs.getTimestamp("offerValidity"));
			obj.setMinimumQuantity(rs.getInt("minimumQuantity"));
			obj.setAvailableQuantity(rs.getInt("availableQuantity"));
			ProductInfo.add(obj);
		}
		db.closeConnection(con);
		return ProductInfo;
	}
	
	public String getcategoryId(String keyword) throws SQLException
	{
		Connection con = db.createConnection();
		String categoryid="";
		String query="select p.categoryId " +
				" from ProductInfo as p, Keywords as k, Stock as s " +
				" where p.productId = k.productId " +
				" and p.productId = s.productId " +
				" and k.keyword = '" + keyword + "'";       
		ResultSet rs=db.executeQuery(query, con);
		
		rs.next();
		categoryid = (rs.getString("categoryId"));
		db.closeConnection(con);
		return categoryid;
	}
	
	public ArrayList<String> getCompanylist(String keyword) throws SQLException
	{
		Connection con = db.createConnection();
		ArrayList<String> companyname = new ArrayList<String>();	
		String query="select distinct(p.brand) " +
				" from ProductInfo as p, Keywords as k " +
				" where p.productId = k.productId " +
				" and k.keyword = '" + keyword + "'";       
		ResultSet rs=db.executeQuery(query, con);
		
		while(rs.next())
		{
			companyname.add(rs.getString("brand"));
		}
		db.closeConnection(con);
		return companyname;
	}
	
	public ArrayList<ProductInfo> getproductinfoforcomparison(ArrayList<String> category,ArrayList<Integer> pidRetrieved) throws SQLException
	{

		Connection con = db.createConnection();
		ArrayList<ProductInfo> ProductInfo = new ArrayList<ProductInfo>();	
		String query = "";
		
		for(int i=0; i<category.size(); i++)
		{
			String categoryid = category.get(i);
			query += "select ProductInfo.productId, ProductInfo.productName, ProductInfo.price, ProductInfo.image, ProductInfo.offer, ProductInfo.categoryId, ProductInfo.description, ProductInfo.brand, ProductInfo.warranty, ProductInfo.offerValidity, Stock.availableQuantity, Stock.minimumQuantity from ProductInfo, Category, Stock where ProductInfo.categoryId = Category.categoryId and  ProductInfo.productId = Stock.productId and Category.categoryId = '" + categoryid + "'";    
			if(i<(category.size()-1))
				query += " Union ";
		}
		
		ResultSet rs=db.executeQuery(query, con);
			
		while(rs.next())
		{
			for(int i=0;i<pidRetrieved.size();i++)
			{
				if(rs.getInt("productId")==pidRetrieved.get(i))
				{
					ProductInfo obj = new ProductInfo();
					obj.setProductID(rs.getInt("productId"));
					obj.setProductName(rs.getString("productName"));
					obj.setPrice(rs.getInt("price"));
					obj.setImage(rs.getString("image"));
					obj.setOffer(rs.getInt("offer"));
					obj.setCategoryID(rs.getString("categoryId"));
					obj.setDescription(rs.getString("description"));
					obj.setBrand(rs.getString("brand"));
					obj.setWarranty(rs.getInt("warranty"));
					obj.setOfferValidity(rs.getTimestamp("offerValidity"));
					obj.setMinimumQuantity(rs.getInt("minimumQuantity"));
					obj.setAvailableQuantity(rs.getInt("availableQuantity"));
					ProductInfo.add(obj);
	
				}
			}
		}
		
		db.closeConnection(con);
			return ProductInfo;
		
		}
	
	
	public ArrayList<ProductInfo> getProductInfoByName(String productname) throws SQLException 
	{
		System.out.println("produc name received here: "+productname);
		Connection con = db.createConnection();
		ArrayList<ProductInfo> productInfoAdded = new ArrayList<ProductInfo>();	
		String query="select p.productId, p.productName, p.price, p.image, p.offer, p.categoryId, p.description, p.brand, p.warranty,p.offerValidity, " +
				" s.availableQuantity, s.minimumQuantity " +
				" from ProductInfo as p, Stock as s " +
				" where  p.productId = s.productId " +
				" and p.productName = '" + productname + "'" ;       
		      
		ResultSet rs=db.executeQuery(query, con);
		ProductInfo obj = new ProductInfo();
		while(rs.next())
		{
			
			obj.setProductID(rs.getInt("productId"));
			obj.setProductName(rs.getString("productName"));
			obj.setPrice(rs.getInt("price"));
			obj.setImage(rs.getString("image"));
			obj.setOffer(rs.getInt("offer"));
			obj.setCategoryID(rs.getString("categoryId"));
			obj.setDescription(rs.getString("description"));
			obj.setBrand(rs.getString("brand"));
			obj.setWarranty(rs.getInt("warranty"));
			obj.setMinimumQuantity(rs.getInt("minimumQuantity"));
			obj.setAvailableQuantity(rs.getInt("availableQuantity"));
			obj.setOfferValidity(rs.getTimestamp("offerValidity"));
			productInfoAdded.add(obj);
		}
		System.out.println("product name from database: "+obj.getProductName());
		db.closeConnection(con);
		return productInfoAdded;		
	}

	public ArrayList<String>  getproductsforcomparison(ArrayList<String> category) throws SQLException 
	{
		Connection con = db.createConnection();
		ArrayList<String> categoryproducts = new ArrayList<String>();	
		String query = "";
		
		for(int i=0; i<category.size(); i++)
		{
			String categoryid = category.get(i);
			query += "select ProductInfo.productName from ProductInfo where ProductInfo.categoryId = '" + categoryid + "'";    
			if(i<(category.size()-1))
				query += " Union ";
		}
		ResultSet rs=db.executeQuery(query, con);
			
		while(rs.next())
		{
			categoryproducts.add(rs.getString("productName"));		
		}
		
		db.closeConnection(con);
		
		return categoryproducts;		
	
	}
	
	public void savePlaceOrderDetails() 
	{
		String status = "PLACED";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");		
		Calendar cal = Calendar.getInstance();
		cal.set(2222, 11, 31);
		java.util.Date deliveryDate = (java.util.Date) cal.getTime();
		Connection con = db.createConnection();
		String query = " INSERT INTO  FlipKartDatabase.Order (status, orderDate, deliveryDate) " +
			           " VALUES (?, ?, ?);";
		PreparedStatement prep;
		try {
			prep = con.prepareStatement(query);
			prep.setString(1, status);
			prep.setString(2, sdf.format(new java.util.Date()));
			prep.setString(3, sdf.format((deliveryDate)));		
			prep.execute();		
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public int saveOrderAddressDetails( custometAddressDetail addressDetail) throws SQLException
	{
		Connection con = db.createConnection();
		DBConnectivity db = new DBConnectivity();	
		int  orderId = 0;
		String query = " Select MAX(orderId) as orderId" +
				       " FROM FlipKartDatabase.Order;";
		ResultSet rs = db.executeQuery(query, con);			
		while(rs.next())
		{
			orderId = Integer.parseInt( rs.getString("orderId"));
		}
		con.close();
		Connection con1 = db.createConnection();
		String query1 = " INSERT INTO FlipKartDatabase.OrderShipingAddress (orderID, customerName, customerEmail, addressLine1, addressLine2,  city, pinCode, customerPhoneNumber) " +
			         	" Values (?, ?, ?, ?, ?, ?, ?, ?);" ;
		PreparedStatement prep = con1.prepareStatement(query1);		
		prep.setInt(1, orderId);
		prep.setString(2, addressDetail.getName());
		prep.setString(3, addressDetail.getEmail());
		prep.setString(4, addressDetail.getAddressLine1());
		prep.setString(5, addressDetail.getAddressLine2());
		prep.setString(6, addressDetail.getCity());
		prep.setString(7, addressDetail.getPinCode());
		prep.setString(8, addressDetail.getPhoneNumber());		
		prep.execute();	
		con1.close();	
		return orderId;
		
	}	
	
	
	public void saveUserOrderDescription( customerCartDetail cart) throws SQLException
	{
		DBConnectivity db = new DBConnectivity();
		Connection con = db.createConnection();
		Connection con1 = db.createConnection();
		int  orderId = 0;
		String query = " Select MAX(orderId) as orderId" +
				       " FROM FlipKartDatabase.Order;";
		ResultSet rs = db.executeQuery(query, con);			
		while(rs.next())
		{
			orderId = Integer.parseInt( rs.getString("orderId"));
		}		
		con.close();
		String query1 = " INSERT INTO FlipKartDatabase.OrderDescription " +
				        " (orderID, productId, quantity, price) " +
				        " Values (?, ?, ?, ?)" ;
		PreparedStatement prep = con1.prepareStatement(query1);		
		prep.setInt(1, orderId);
		prep.setInt(2, cart.getProductID());
		prep.setInt(3, cart.getQuantity() );
		prep.setFloat(4, Float.parseFloat( cart.getPrice() ) );
		prep.execute();	
		con1.close();			
	}
	
	public void clearUserCart(String email) throws NumberFormatException, SQLException
	{
		DBConnectivity db = new DBConnectivity();
		Connection con = db.createConnection();
		Connection con1 = db.createConnection();
		int  userId = 0;	
		
		String query = " SELECT userId " +
				       " FROM FlipKartDatabase.UserCredantials" +
				       " WHERE email = '" + email + "';" ;
		ResultSet rs = db.executeQuery(query, con);			
		while(rs.next())
		{
			userId = Integer.parseInt( rs.getString("userId"));
		}		
		con.close();
		String query1 = " DELETE" +
				        " FROM FlipKartDatabase.Cart" +
				        " WHERE userID = " + userId + ";" ;		
		Statement st=(Statement) con1.createStatement();
		st.executeUpdate(query1);				
		con1.close();			
	}
	
	public custometAddressDetail getUserAddressDetail(String email) throws SQLException
	{
		Connection con = db.createConnection();
		DBConnectivity db = new DBConnectivity();
		custometAddressDetail addressDetails = new custometAddressDetail();	
		String query = " SELECT  CONCAT(firstName, ' ', lastName) as name, addressLine1, addressLine2, pinCode,phoneNumber, city " +
				       " FROM `FlipKartDatabase`.`UserCredantials` " +
				       " WHERE email = '" + email + "' " ;
		ResultSet rs = db.executeQuery(query, con);			
		while(rs.next())
		{				
			addressDetails.setName(rs.getString("name"));
			addressDetails.setPhoneNumber(rs.getString("phoneNumber"));
			addressDetails.setEmail(email);				
			addressDetails.setPinCode(rs.getString("pinCode"));
			addressDetails.setAddressLine1(rs.getString("addressLine1"));
			addressDetails.setAddressLine2(rs.getString("addressLine2"));
			addressDetails.setCity(rs.getString("city"));				
		}			
		db.closeConnection(con);
		return addressDetails;
	}
	
	public ArrayList<customerCartDetail>  getCartTableDetail(String email) throws SQLException
	{
		ArrayList<customerCartDetail> cartDetailsList = new ArrayList<customerCartDetail>();
		
		Connection con = db.createConnection();
		DBConnectivity db = new DBConnectivity();	
		
		String query = 	" SELECT  P.image as image, P.productName as productName,P.productId  as productId, C.quantity as quantity, " + 
		        		"	CASE	" + 
		                "		WHEN P.offerValidity >= CURDATE() THEN (P.price -IFNULL(P.offer, 0 ) ) " + 
		                "		ELSE P.price " + 
		                "	END as price " +
		                " FROM FlipKartDatabase.UserCredantials AS U " + 
		                " INNER JOIN FlipKartDatabase.Cart AS C " + 
		                "		ON C.userId = U.userId " + 
		                " INNER JOIN FlipKartDatabase.ProductInfo AS P " +    
		                " 		ON P.productId = C.productId " +
		                " WHERE email =   '" + email + "' " ;
		ResultSet rs = db.executeQuery(query, con);			
		while(rs.next())
		{
			customerCartDetail cartDetail = new customerCartDetail();					
			cartDetail.setImage(rs.getString("image"));				
			cartDetail.setProductName(rs.getString("productName"));
			cartDetail.setProductID( Integer.parseInt( rs.getString("productId") ) );
			cartDetail.setQuantity(Integer.parseInt(rs.getString("quantity") ));				
			cartDetail.setPrice(rs.getString("price"));	
			cartDetail.setSubTotal(	Float.toString(Float.parseFloat( cartDetail.getPrice() ) * (  cartDetail.getQuantity()	 )	)  );
			cartDetailsList.add(cartDetail);				
		}		
		db.closeConnection(con);
		return cartDetailsList;
	}

	
	public ArrayList<customerCartDetail>  getCartCokkiesDetail( ArrayList<CartProduct>  cartDetails) throws SQLException
	{
		ArrayList<customerCartDetail> cartDetailsList = new ArrayList<customerCartDetail>();			
		Connection con = db.createConnection();
		
		for(CartProduct p : cartDetails)
		{		
			String query = " SELECT P.image as image, P.productId as productId, P.productName as productName, " +
					 	   " 		CASE " +
					 	   "				WHEN P.offerValidity >= CURDATE() THEN (P.price -IFNULL(P.offer, 0 ) ) " +
					       "				ELSE P.price " + 
					       "		END as price " + 
					       " FROM FlipKartDatabase.ProductInfo    as P " +
					       " WHERE P.productId = "+p.getProductId()+";";
			ResultSet rs=db.executeQuery(query, con);
			while(rs.next())
			{
				customerCartDetail cartDetail = new customerCartDetail();									
				cartDetail.setImage(rs.getString("image"));				
				cartDetail.setProductName(rs.getString("productName"));
				cartDetail.setProductID(Integer.parseInt(rs.getString("productId")));
				cartDetail.setQuantity(p.getQuantity());		
				cartDetail.setPrice(rs.getString("price"));	
				cartDetail.setSubTotal(	Float.toString(Float.parseFloat( cartDetail.getPrice() ) * (  cartDetail.getQuantity()	 )	)  );				
				cartDetailsList.add(cartDetail);
			}
		}		
		
		db.closeConnection(con);					
		return cartDetailsList;
	}
	
	public void insertOrderPaymentDetails(String orderId, String bankName, String grandTotal, String paymentType, String accountNumber) throws SQLException {
		// TODO Auto-generated method stub
		DBConnectivity db = new DBConnectivity();
		Connection con = db.createConnection();
		String query1 = " INSERT INTO  FlipKartDatabase.Payment " +
						" (orderID, bank, paymentType, amount, paymentDate, payeeAccountNumber) " +
						" VALUES (?, ?, ?, ?, ?, ?);" ;
		PreparedStatement prep = con.prepareStatement(query1);		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");		
		prep.setInt(1, Integer.parseInt(orderId) );
		prep.setString(2, bankName);
		prep.setString(3, paymentType );
		prep.setFloat(4, Float.parseFloat( grandTotal ) );
		prep.setString(5, sdf.format(new java.util.Date()));
		prep.setString(6, accountNumber );		
		prep.execute();	
		con.close();			
	}	
	
	public void deactivateAccount(String email) throws SQLException {
		// TODO Auto-generated method stub
		DBConnectivity db = new DBConnectivity();
		Connection con = db.createConnection();
		
		String query = "DELETE FROM FlipKartDatabase.UserCredantials WHERE  email = '" + email + "' ";
		Statement st=(Statement) con.createStatement();
		st.executeUpdate(query);
		
	}	
	
	//Function to insert feedback comments into database
	public void insertFeedback(FeedbackModel feedbck) throws SQLException
	{
		
		Connection con = db.createConnection();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String date = sdf.format(new java.util.Date());
		String query = "Insert into Feedback values(?,?,?,?,?)";
		PreparedStatement prepStmnt = con.prepareStatement(query);
		prepStmnt.setString(1,feedbck.getEmail());
		prepStmnt.setString(2, feedbck.getMobileNumber());
		prepStmnt.setString(3, feedbck.getCategory());
		prepStmnt.setString(4, feedbck.getMessage());
		prepStmnt.setString(5, date);
		
		prepStmnt.execute();
		con.close();
	}
	

	public String verifyCardDetails(String cardNumber, String expireMonth, String expireYear, String cvv) throws SQLException
	{		
		String bankName = null ;
		Connection con = db.createConnection();
		DBConnectivity db = new DBConnectivity();
		
		String query = 	" SELECT bankName" +
						" FROM FlipKartDatabase.BankDetails " +
						" WHERE creditCardNumber = " +cardNumber + 
						"	 	AND MONTH(expiryDate) = '" + expireMonth + "' " +
						"		AND YEAR(expiryDate) = '" + expireYear +" ' " +
						"		AND cvv = '" + cvv + "';";
		
		ResultSet rs=db.executeQuery(query, con);
		if(rs.next())
		{
			bankName = rs.getString("bankName");
		}
		
		return bankName;
	}

	
	public void updatePaymentInAccount(String cardNumber, String grandTotal) throws SQLException {

		DBConnectivity db = new DBConnectivity();
		Connection con = db.createConnection();		
		Statement stmt = (Statement) con.createStatement();
		
		String query1 = "UPDATE FlipKartDatabase.BankDetails " +
						" SET balance = balance - " + grandTotal + 
						" WHERE creditCardNumber = " + cardNumber;
		 int rows = stmt.executeUpdate(query1);		
		String query2 = "UPDATE FlipKartDatabase.BankDetails " +
						" SET balance = balance + "	+ grandTotal + 
						" WHERE creditCardNumber = 1";
		rows = stmt.executeUpdate(query2);			
		con.close();			
	}

	public void updateProductQuantityAfterPurchase(String orderId) throws SQLException {
		// TODO Auto-generated method stub
		int pId  = 0;
		int qty = 0;
		String query1;
		String query = 	" SELECT productId , quantity " +
						" From FlipKartDatabase.OrderDescription " +
						" Where orderID = "+orderId ;
		Connection con = db.createConnection();
		DBConnectivity db = new DBConnectivity();
		ResultSet rs=db.executeQuery(query, con);
		while(rs.next())
		{
			pId = rs.getInt("productId");
			qty = rs.getInt("quantity");
			query1 = " Update FlipKartDatabase.Stock " +
					 " Set availableQuantity = availableQuantity - " + qty + 
					 " Where productId = " + pId ;
			Statement stmt = (Statement) con.createStatement();
			stmt.executeUpdate(query1);
		}		
	}
	
	public String validateBankLogin(String bankLoginId,String bankPassword, String bankName) throws SQLException
	{
		String creditCardNumber =null;
		Connection con = db.createConnection();
		String query =  " SELECT creditCardNumber, transactionPassword" +
						" FROM FlipKartDatabase.BankDetails " +
						" WHERE bankLoginId= '"+bankLoginId+"' " +
						"		And bankName = '" + bankName + "';";		
		ResultSet rs=db.executeQuery(query, con);
		while(rs.next())
		{
			String password = rs.getString("transactionPassword");			
			if(password.equals(bankPassword))
			{
				creditCardNumber = rs.getString("creditCardNumber");				
			}
		}
		con.close();
		return creditCardNumber;
	}

	public ArrayList<customerCartDetail> generateReceipt(int orderId) throws SQLException
	{
		ArrayList<customerCartDetail> productDetailList = new ArrayList<customerCartDetail>();
		String query = " SELECT PI.productName, OD.price, OD.quantity, ( (OD.quantity) * (OD.price) ) as subTotal, P.transactionID " +  
								" FROM FlipKartDatabase.OrderDescription as OD "  + 
								" Inner Join FlipKartDatabase.Payment as P " +
								"		ON P.orderId = OD.orderID " +
								" Inner Join FlipKartDatabase.ProductInfo as PI " +
								"		ON OD.productId = PI.productId WHERE OD.orderId = " + orderId;
		Connection con = db.createConnection();
		DBConnectivity db = new DBConnectivity();
		ResultSet rs=db.executeQuery(query, con);
		while(rs.next())
		{
			customerCartDetail productDetails = new customerCartDetail();
			productDetails.setProductName(rs.getString("productName"));
			productDetails.setPrice(rs.getString("price"));
			productDetails.setQuantity(rs.getInt("quantity"));
			productDetails.setSubTotal(rs.getString("subTotal"));
			productDetails.setTransactionId(rs.getString("transactionId" ) );	
			productDetailList.add(productDetails);
		}		
		return productDetailList;		
	}

	public getUserBankDetails getUserBankDetail(String bankLoginId) throws SQLException 
	{
		DBConnectivity db=new DBConnectivity();
		Connection con=db.createConnection();
		String query = " SELECT nameofAccountHolder,accountNumber,balance" +
					   " FROM FlipKartDatabase.BankDetails " +
					   " Where bankLoginId ='"+bankLoginId+"'";
		ResultSet rs=db.executeQuery(query, con);
		getUserBankDetails user = new getUserBankDetails();
		while(rs.next())
		{
			user.setCustomerName(rs.getString("nameofAccountHolder"));
			user.setAccountNumber(rs.getString("accountNumber"));
			user.setBalance(rs.getString("balance"));			
		}
		return user;
	}

	public String verifyBalanceDetails(String accountNumber) throws SQLException 
	{
		DBConnectivity db=new DBConnectivity();
		Connection con=db.createConnection();		
		String query = " SELECT balance   " +
				       " FROM FlipKartDatabase.BankDetails " +
				       " Where accountNumber ='"+accountNumber + "';";		
		ResultSet rs=db.executeQuery(query, con);
		getUserBankDetails user = new getUserBankDetails();
		String balance = null;
		while(rs.next())
		{
			balance = (rs.getString("balance"));
		}
		return balance;
	}

	public ArrayList<String> getUserEmailIdForOrder(int orderId) throws SQLException
	{
		ArrayList<String> receipt = new ArrayList<String>();
		DBConnectivity db=new DBConnectivity();
		Connection con=db.createConnection();		
		String query = " SELECT SA.customerEmail, SA.customerName, P.transactionId   " +
				       " FROM FlipKartDatabase.OrderShipingAddress as SA " +
				       " Inner Join FlipKartDatabase.Payment as P " +
				       " 	On P.orderId = SA.orderId" +				       
				       " Where SA.orderId ='"+ orderId + "';";		
		ResultSet rs = db.executeQuery(query, con);
		while(rs.next())
		{
			receipt.add(rs.getString("customerEmail"));
			receipt.add(rs.getString("customerName"));
			receipt.add(rs.getString("transactionId"));
		}			
		return receipt;
	}
	public String getCategoryIdForRetrieval(ArrayList<CompareCartProduct> cartProducts) 
	{
		String category=null;
		for(CompareCartProduct p : cartProducts)
		{
			category = p.getCategory();			
		}
		return category;
		}

	public ArrayList<Integer> getProductIdForRetrieval(ArrayList<CompareCartProduct> cartProducts) 
	{
		ArrayList<Integer> pidRetrieved= new ArrayList <Integer>();
		for(CompareCartProduct p : cartProducts)
		{
			pidRetrieved.add(p.getProductId());			
		}
		return pidRetrieved;
	}


}


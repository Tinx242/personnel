package jdbc;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import personnel.*;

public class JDBC implements Passerelle 
{
	Connection connection;

	public JDBC()
	{
		try
		{
			Class.forName(Credentials.getDriverClassName());
			connection = DriverManager.getConnection(Credentials.getUrl(), Credentials.getUser(), Credentials.getPassword());
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Pilote JDBC non installé.");
		}
		catch (SQLException e)
		{
			System.out.println(e);
		}
	}
	
	@Override
	public GestionPersonnel getGestionPersonnel() 
	{
	    GestionPersonnel gestionPersonnel = new GestionPersonnel();
	    try 
	    {
	        Statement instruction = connection.createStatement();
	        String requeteRoot = "SELECT * FROM employe WHERE num_ligue IS NULL";
	        ResultSet rsRoot = instruction.executeQuery(requeteRoot);

	        if (rsRoot.next()) 
	        {
	            gestionPersonnel.addRoot(
	                rsRoot.getInt("num_employe"),
	                rsRoot.getString("nom"),
	                rsRoot.getString("password")
	            );
	        } 
	        else 
	        {
	            gestionPersonnel.getRoot().setId(insert(gestionPersonnel.getRoot()));
	        }
	        
	        String requete = "select * from ligue";
	        ResultSet ligues = instruction.executeQuery(requete);
	        while (ligues.next())
	            gestionPersonnel.addLigue(ligues.getInt(1), ligues.getString(2));
	    }
	    catch (SQLException e)
	    {
	        System.out.println(e);
	    }
	    catch (SauvegardeImpossible e)
	    {
	        e.printStackTrace();
	    }
	    return gestionPersonnel;
	}

	@Override
	public void sauvegarderGestionPersonnel(GestionPersonnel gestionPersonnel) throws SauvegardeImpossible 
	{
		close();
	}
	
	public void close() throws SauvegardeImpossible
	{
		try
		{
			if (connection != null)
				connection.close();
		}
		catch (SQLException e)
		{
			throw new SauvegardeImpossible(e);
		}
	}
	
	@Override
	public int insert(Ligue ligue) throws SauvegardeImpossible 
	{
		try 
		{
			PreparedStatement instruction;
			instruction = connection.prepareStatement("insert into ligue (nom) values(?)", Statement.RETURN_GENERATED_KEYS);
			instruction.setString(1, ligue.getNom());		
			instruction.executeUpdate();
			ResultSet id = instruction.getGeneratedKeys();
			id.next();
			return id.getInt(1);
		} 
		catch (SQLException exception) 
		{
			exception.printStackTrace();
			throw new SauvegardeImpossible(exception);
		}		
	}
	
	@Override
	public void update(Ligue ligue) throws SauvegardeImpossible
	{
		try
		{
			PreparedStatement instruction;
			instruction = connection.prepareStatement("update ligue set nom = ? where num_ligue = ?");
			instruction.setString(1, ligue.getNom());
			instruction.setInt(2, ligue.getId());
			instruction.executeUpdate();
		}
		catch (SQLException exception)
		{
			exception.printStackTrace();
			throw new SauvegardeImpossible(exception);
		}
	}
	
	@Override
	public int insert(Employe employe) throws SauvegardeImpossible
	{
	    try
	    {
	        PreparedStatement instruction;
	        instruction = connection.prepareStatement(
	            "insert into employe (nom, prenom, mail, password, date_arrivee, date_depart) values(?, ?, ?, ?, ?, ?)",
	            Statement.RETURN_GENERATED_KEYS
	        );
	        instruction.setString(1, employe.getNom());
	        instruction.setString(2, employe.getPrenom());
	        instruction.setString(3, employe.getMail());
	        instruction.setString(4, employe.getPassword());
	        instruction.setObject(5, employe.getDateArrivee());
	        instruction.setObject(6, employe.getDateDepart());
	        instruction.executeUpdate();
	        ResultSet id = instruction.getGeneratedKeys();
	        id.next();
	        return id.getInt(1);
	    }
	    catch (SQLException exception)
	    {
	        exception.printStackTrace();
	        throw new SauvegardeImpossible(exception);
	    }
	}
}

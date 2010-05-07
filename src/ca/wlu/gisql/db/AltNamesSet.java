package ca.wlu.gisql.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class AltNamesSet extends LazySqlSet<String> {

	private final Connection connection;
	private final long id;

	public AltNamesSet(long id, Connection connection) {
		super();
		this.id = id;
		this.connection = connection;
	}

	@Override
	protected void prepare(Set<String> set) throws SQLException {
		PreparedStatement statement = connection
				.prepareStatement("SELECT altname FROM altnames WHERE gene = ?");
		statement.setLong(1, id);
		ResultSet rs = statement.executeQuery();
		while (rs.next()) {
			set.add(rs.getString(1));
		}
		rs.close();
		statement.close();
	}

}

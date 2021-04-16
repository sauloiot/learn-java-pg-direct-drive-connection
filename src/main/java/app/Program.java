package app;

import db.DB;
import entities.Order;
import entities.OrderStatus;
import entities.Product;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Program {

	public static void main(String[] args) throws SQLException {
		
		Connection conn = DB.getConnection();
	
		Statement st = conn.createStatement();
			
		ResultSet rs = st.executeQuery("SELECT * FROM tb_order " +
				"INNER JOIN tb_order_product ON tb_order.id = tb_order_product.order_id " +
				"INNER JOIN tb_product ON tb_product.id = tb_order_product.product_id");

		Map<Long, Order> map = new HashMap<>();
		Map<Long, Product> prods = new HashMap<>();
		while (rs.next()) {
			Long orderId = rs.getLong("order_id");
			if(map.get(orderId) == null){
				Order order = instantiateOrder(rs);
				map.put(orderId, order);
			}

			Long productId = rs.getLong("product_id");
			if (prods.get(productId) == null){
				Product p = instantiateProduct(rs);
				prods.put(productId, p);
			}

			map.get(orderId).getProducts().add(prods.get(productId));

		}
		for (Long orderId : map.keySet()){
			System.out.println(map.get(orderId));
			for (Product p : map.get(orderId).getProducts()){
				System.out.println(p);
			}
			System.out.println();
		}
	}

	private static Product instantiateProduct(ResultSet rs) throws SQLException {
		Product p = new Product();
		p.setId(rs.getLong("product_id"));
		p.setDescription(rs.getString("description"));
		p.setName(rs.getString("name"));
		p.setImageUri(rs.getString("image_uri"));
		p.setPrice(rs.getDouble("price"));
		return p;
	}

	private static Order instantiateOrder(ResultSet rs) throws SQLException {
		Order o = new Order();
		o.setId(rs.getLong("order_id"));
		o.setLatitude(rs.getDouble("latitude"));
		o.setLongitude(rs.getDouble("longitude"));
		o.setMoment(rs.getTimestamp("moment").toInstant());
		o.setStatus(OrderStatus.values()[rs.getInt("status")]);
		return o;
	}
}

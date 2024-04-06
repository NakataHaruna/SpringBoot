package com.example.market.entities;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item")
@Entity
public class Item {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id
	
	@ManyToOne(fetch = FetchType.EAGER)
    private User user; // ユーザーid
	
	@ManyToOne(fetch = FetchType.EAGER)
    private Category category; // カテゴリー
	
	// カート を追記
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="cart",
        joinColumns = @JoinColumn(name="item_id", referencedColumnName="id"),
        inverseJoinColumns = @JoinColumn(name="user_id", referencedColumnName="id"))
    private Set<User> orderedUsers = new HashSet<User>();
	
    // お気に入り を追記
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="books",                                                       
        joinColumns = @JoinColumn(name="item_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name="user_id", referencedColumnName = "id"))
    private Set<User> likedUsers = new HashSet<>();
    
	@Column(name = "name", length = 255, nullable = false)
    private String name; // 商品名
	
	@Column(name = "description", length = 1000, nullable = false)
    private String description; // 商品の説明
	
	@Column(name = "price", length = 200, nullable = false)
    private int price; // 価格
 
    @Column(name = "image", length = 100, nullable = false)
    private String image;  // 商品画像ファイル名
    
    @Column(name = "stock", nullable = false)
    private int stock; // 出品商品の在庫数
    
    // 作成日時
    @Column(name="createdAt",nullable = false, updatable = false, insertable = false, 
    columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private ZonedDateTime createdAt;
 
    // 更新日時
    @Column(name="updatedAt",nullable = false, updatable = false, insertable = false, 
    columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private ZonedDateTime updatedAt;
    
    public boolean isSoldOut() {
		return orderedUsers.size() > 0;     // orderedUsersが1件以上存在するということは購入済みと判断できますので、このメソッドがtrueを返す場合は「売り切れ」、falseを返す場合は「出品中」となります
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		return Objects.equals(description, other.description) && Objects.equals(id, other.id)
				&& Objects.equals(name, other.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, id, name);
	}

}

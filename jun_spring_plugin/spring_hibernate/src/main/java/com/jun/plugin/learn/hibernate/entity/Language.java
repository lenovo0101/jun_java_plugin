/*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009, Red Hat, Inc. and/or its affiliates or third-
  * party contributors as indicated by the @author tags or express 
  * copyright attribution statements applied by the authors.  
  * All third-party contributions are distributed under license by 
  * Red Hat, Inc.
  *
  * This copyrighted material is made available to anyone wishing to 
  * use, modify, copy, or redistribute it subject to the terms and 
  * conditions of the GNU Lesser General Public License, as published 
  * by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of 
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public 
  * License along with this distribution; if not, write to:
  * 
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */

package com.jun.plugin.learn.hibernate.entity;
import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author Sharath Reddy
 */
@Entity
@Table(name="test_language")
public class Language implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String code;
	private List<Message> messages;

	@Id
	@Column(name="code")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/**  
	 * 返回 messages 的值   
	 * @return messages  
	 */
	@OneToMany(cascade=CascadeType.ALL)
	public List<Message> getMessages() {
		return messages;
	}
	/**  
	 * 设置 messages 的值  
	 * @param messages
	 */
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	
	
}

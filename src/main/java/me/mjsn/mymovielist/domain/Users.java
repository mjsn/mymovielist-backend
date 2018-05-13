package me.mjsn.mymovielist.domain;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "Users", types = { User.class }) 
interface Users { 

	String getUsername(); 

}
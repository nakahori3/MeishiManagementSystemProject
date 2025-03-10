package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AuthorityEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.form.UserForm;
import com.example.demo.repository.AuthorityRepository;
import com.example.demo.repository.UsersRepository;

@Service
public class UserService {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
    
	@Autowired
	UserDetailsManager userDetailsManager;
	
	@Autowired
	UsersRepository usersRepository;
	
	@Autowired
	public AuthorityRepository authRepository;
	
	//UserDetailsオブジェクトを生成
	public UserDetails makeUser(String user, String pass) {
        return User.withUsername(user)
                   .password(passwordEncoder.encode(pass))
                   .roles("GENERAL")
                   .disabled(false)
                   .build();
	}
	
	
	// usersテーブルから登録されている全てのユーザーを取得
    public List<UserEntity> makeUsersList() {
        Iterable<UserEntity> userList = usersRepository.findAll();

        List<UserEntity> usersList = new ArrayList<>();
        for (UserEntity user : userList) {
            usersList.add(user);
        }
        return usersList;
    }
	
	//全てのユーザーリストを取得
	public List<AuthorityEntity> getUserAuthList(){
		Iterable<AuthorityEntity> list = authRepository.findAllByOrderByIdAsc();
		
		List<AuthorityEntity> authList = new ArrayList<>();
		for(AuthorityEntity auth : list) {
			authList.add(auth);
		}
		return authList;
	}
	
	//ユーザー名からユーザーIDを取得
    public int getUseridByUsername(String username){
    	UserEntity user=usersRepository.findUseridByUsername(username);
    	return user.getId();
    }
	
    
    //ユーザー名からユーザーエンティティを取得
    public UserEntity getUserEntitybyUsername(String username) {
    	return usersRepository.findByUsernameOrderByIdAsc(username);
    }
    
    //ユーザー名からユーザーオーソリティを取得
    public String getAuthorityByUsername(String username) {
    	AuthorityEntity UserAuthority = authRepository.findByAuthority(username);
    	return UserAuthority.getAuthority();
    }
    
    //ユーザが登録されているか確認する
    public boolean userExistsCheck(String username) {
    	return userDetailsManager.userExists(username);
    }

    //ユーザー名からユーザーオーソリティエンティティを取得する
	public AuthorityEntity getUserAuthorityByUsername(String username) {
		return authRepository.findByUsername(username);
	}
	

	
	//新規ユーザー登録
    public void register(UserForm userForm) {
    	userDetailsManager.createUser(makeUser(userForm.getUsername(),userForm.getPassword()));
	}


 // 権限をADMINからGENERALにする
    public void updateAuthorityToGeneral(String username) {
        // ユーザーの権限情報を取得
        AuthorityEntity authorityEntity = authRepository.findByUsername(username);
        // GENERALへの権限変更
        authorityEntity.setAuthority("ROLE_GENERAL");
        // 権限情報を保存
        authRepository.save(authorityEntity);
    }
    
    // 権限をGENERALからADMINにする
    public void updateAuthorityToAdmin(String username) {
        // ユーザーの権限情報を取得
        AuthorityEntity authorityEntity = authRepository.findByUsername(username);
        // ADMINへの権限変更
        authorityEntity.setAuthority("ROLE_ADMIN");
        // 権限情報を保存
        authRepository.save(authorityEntity);
    }
    
    
    
    public void deleteUserByUsername(String username) {
        // ユーザー削除処理
        AuthorityEntity authorityEntity = authRepository.findByUsername(username);
        if (authorityEntity != null) {
            authRepository.delete(authorityEntity); // 権限情報を削除
        }

        UserEntity userEntity = usersRepository.findByUsernameOrderByIdAsc(username);
        if (userEntity != null) {
            usersRepository.delete(userEntity); // ユーザー情報を削除
        }

        System.out.println("ユーザー削除完了: " + username);
    }


}

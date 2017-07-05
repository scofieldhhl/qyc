package com.systemteam.user;

import android.text.TextUtils;

/**
 * @author rivers
 * @version 1.0
 * @Function 获取用户信息
 * @date ${DATA}
 */
public class UserInfoBean {

    private String member_id;
    private String uid;
    private String firstname;
    private String lastname;
    private String email;
    private String created;
    private String status;

    /**
     * 头像
     */
    private String avatar;
    private String nickname;
    private String title;
    private String company;
    private String homepage;

    private String fax;
    private String environment;
    private String address;
    private String city;
    private String state;

    private String zip;
    private String country;
    private String phone;
    private String purchase_date;
    private String customer_type;

    private String referer_type;
    private String login_num;
    private String last_login;
    private String current_login;
    private String last_ip;

    private String country_short_name;
    private String customer_visit_str;
    private String income_range;
    private String education_degree;
    private String sex;

    private String birthday;
    private String age;
    private String profession;
    private String memorial_day;
    private String others_info;

    private String recommend_product;
    private String spare_email;
    private String msn;
    private String company_industry;
    private String blog;

    private String remark;
    private String ip;
    private String social_network;
    private String sku_type;
    private String vc;

    private String timestamp;
    private String cc;

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
//        return nickname;
        if (!TextUtils.isEmpty(firstname) || !TextUtils.isEmpty(lastname)) {
            return firstname + " " + lastname;
        } else {
            return nickname;
        }
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPurchase_date() {
        return purchase_date;
    }

    public void setPurchase_date(String purchase_date) {
        this.purchase_date = purchase_date;
    }

    public String getCustomer_type() {
        return customer_type;
    }

    public void setCustomer_type(String customer_type) {
        this.customer_type = customer_type;
    }

    public String getReferer_type() {
        return referer_type;
    }

    public void setReferer_type(String referer_type) {
        this.referer_type = referer_type;
    }

    public String getLogin_num() {
        return login_num;
    }

    public void setLogin_num(String login_num) {
        this.login_num = login_num;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public String getCurrent_login() {
        return current_login;
    }

    public void setCurrent_login(String current_login) {
        this.current_login = current_login;
    }

    public String getLast_ip() {
        return last_ip;
    }

    public void setLast_ip(String last_ip) {
        this.last_ip = last_ip;
    }

    public String getCountry_short_name() {
        return country_short_name;
    }

    public void setCountry_short_name(String country_short_name) {
        this.country_short_name = country_short_name;
    }

    public String getCustomer_visit_str() {
        return customer_visit_str;
    }

    public void setCustomer_visit_str(String customer_visit_str) {
        this.customer_visit_str = customer_visit_str;
    }

    public String getIncome_range() {
        return income_range;
    }

    public void setIncome_range(String income_range) {
        this.income_range = income_range;
    }

    public String getEducation_degree() {
        return education_degree;
    }

    public void setEducation_degree(String education_degree) {
        this.education_degree = education_degree;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getMemorial_day() {
        return memorial_day;
    }

    public void setMemorial_day(String memorial_day) {
        this.memorial_day = memorial_day;
    }

    public String getOthers_info() {
        return others_info;
    }

    public void setOthers_info(String others_info) {
        this.others_info = others_info;
    }

    public String getRecommend_product() {
        return recommend_product;
    }

    public void setRecommend_product(String recommend_product) {
        this.recommend_product = recommend_product;
    }

    public String getSpare_email() {
        return spare_email;
    }

    public void setSpare_email(String spare_email) {
        this.spare_email = spare_email;
    }

    public String getMsn() {
        return msn;
    }

    public void setMsn(String msn) {
        this.msn = msn;
    }

    public String getCompany_industry() {
        return company_industry;
    }

    public void setCompany_industry(String company_industry) {
        this.company_industry = company_industry;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSocial_network() {
        return social_network;
    }

    public void setSocial_network(String social_network) {
        this.social_network = social_network;
    }

    public String getSku_type() {
        return sku_type;
    }

    public void setSku_type(String sku_type) {
        this.sku_type = sku_type;
    }

    public String getVc() {
        return vc;
    }

    public void setVc(String vc) {
        this.vc = vc;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    @Override
    public String toString() {
        return "UserInfoBean{" +
                "member_id='" + member_id + '\'' +
                ", uid='" + uid + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", created='" + created + '\'' +
                ", status='" + status + '\'' +
                ", avatar='" + avatar + '\'' +
                ", nickname='" + nickname + '\'' +
                ", title='" + title + '\'' +
                ", company='" + company + '\'' +
                ", homepage='" + homepage + '\'' +
                ", fax='" + fax + '\'' +
                ", environment='" + environment + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                ", country='" + country + '\'' +
                ", phone='" + phone + '\'' +
                ", purchase_date='" + purchase_date + '\'' +
                ", customer_type='" + customer_type + '\'' +
                ", referer_type='" + referer_type + '\'' +
                ", login_num='" + login_num + '\'' +
                ", last_login='" + last_login + '\'' +
                ", current_login='" + current_login + '\'' +
                ", last_ip='" + last_ip + '\'' +
                ", country_short_name='" + country_short_name + '\'' +
                ", customer_visit_str='" + customer_visit_str + '\'' +
                ", income_range='" + income_range + '\'' +
                ", education_degree='" + education_degree + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", age='" + age + '\'' +
                ", profession='" + profession + '\'' +
                ", memorial_day='" + memorial_day + '\'' +
                ", others_info='" + others_info + '\'' +
                ", recommend_product='" + recommend_product + '\'' +
                ", spare_email='" + spare_email + '\'' +
                ", msn='" + msn + '\'' +
                ", company_industry='" + company_industry + '\'' +
                ", blog='" + blog + '\'' +
                ", remark='" + remark + '\'' +
                ", ip='" + ip + '\'' +
                ", social_network='" + social_network + '\'' +
                ", sku_type='" + sku_type + '\'' +
                ", vc='" + vc + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", cc='" + cc + '\'' +
                '}';
    }
}

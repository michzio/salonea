package pl.salonea.entities;

import pl.salonea.constraints.ChronologicalAccountDates;
import pl.salonea.constraints.Email;
import pl.salonea.constraints.EmailAvailability;
import pl.salonea.constraints.StrongPassword;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name ="account_type", discriminatorType = DiscriminatorType.STRING,
         columnDefinition = "ENUM('natural_person', 'firm', 'provider', 'employee', 'administrator', 'moderator', 'user')")
@DiscriminatorValue("user")
@Access(AccessType.PROPERTY)
@Table(name = "user_account")
@NamedQueries({
        @NamedQuery(name="findAllNotActivated", query="SELECT u FROM UserAccount u WHERE u.activationCode IS NOT NULL"),
        @NamedQuery(name="findAllActivated", query="SELECT u FROM UserAccount u WHERE u.activationCode IS NULL"),
        @NamedQuery(name="findForEmail", query="SELECT u FROM UserAccount u WHERE u.email = :email"),
        @NamedQuery(name="findCreatedBetween", query="SELECT u FROM UserAccount u WHERE u.registrationDate >= :fromDate AND u.registrationDate <= :toDate")
})
@ChronologicalAccountDates
public class UserAccount implements Serializable {

    private static final Logger logger = Logger.getLogger(UserAccount.class.getName());

    private Long userId;
    private String email;
    private String login;
    private String password;
    private String activationCode; // user account MD5 activation code, NULL when activated
    private Date registrationDate;
    private Date lastLogged;
    private Date lastFailedLogin;

    private String accountType; // discriminator

    /* Constructors */
    public UserAccount() {
    }

    public UserAccount(String email, String login, String password)  {
        this.email = email;
        this.login = login;
        this.password = password;
        this.registrationDate = new Date();

        generateActivationCode();

    }

    /* Getters and setters */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false) // runtime scoped not null, whereas nullable references only to table
    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Email
    @EmailAvailability
    @Column(name = "email", nullable = false, unique = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NotNull
    @Size(min=2, max=45)
    @Column(name = "login", length = 45, nullable = false)
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @StrongPassword
    @Column(name = "password", length = 45, nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Past @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "registration_date", nullable = false, columnDefinition = "DATETIME")
    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Past
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_logged", columnDefinition = "DATETIME default NULL")
    public Date getLastLogged() {
        return lastLogged;
    }

    public void setLastLogged(Date lastLogged) {
        this.lastLogged = lastLogged;
    }

    @Past
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_failed_login", columnDefinition = "DATETIME default NULL")
    public Date getLastFailedLogin() {
        return lastFailedLogin;
    }

    public void setLastFailedLogin(Date lastFailedLogin) {
        this.lastFailedLogin = lastFailedLogin;
    }

    @Column(name = "activation_code", length = 32, columnDefinition="CHAR(32) default NULL")
    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String generateActivationCode() {

        // generating MD5 activation code when creating user
        // based on email address and registration date
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String input = email + formatter.format(registrationDate);

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(input.getBytes());
            BigInteger bigInt = new BigInteger(1, messageDigest.digest());
            String hashMD5 = bigInt.toString(16);

            this.activationCode = hashMD5;

        } catch( NoSuchAlgorithmException e) {
            logger.log(Level.INFO, "Exception when generating MD5 activation code... no such algorithm.");
        }

        return this.activationCode;
    }

    @Column(name = "account_type", insertable = false, updatable = false)
    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
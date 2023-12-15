package CAS;
import java.util.concurrent.atomic.AtomicReference;

public class CASDemo {
    public static void main(String[] args) {
        
        AtomicReference<User> auAtomicReference = new AtomicReference<>();

        User z3 = new User("z3", 22);
        User li4 = new User("li4", 28);

        auAtomicReference.set(z3);
        System.out.println(auAtomicReference.compareAndSet(z3, li4)+"\t"+auAtomicReference.get().toString());
        System.out.println(auAtomicReference.compareAndSet(z3, li4)+"\t"+auAtomicReference.get().toString());
    }    
}

class User{
    String userName;
    int age;

    public void setAge(int age) {
        this.age = age;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public String getUserName() {
        return userName;
    }

    public User(String userName,int age){
        this.userName = userName;
        this.age = age;
    }

    public String toString(){
        return "{userName:"+userName+",age:"+age+"}";
    }
}
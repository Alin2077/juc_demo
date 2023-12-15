package Future;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CompletableFutureMallDemo {
    
    static List<NetMall> list = Arrays.asList(
        new NetMall("jd"),
        new NetMall("dangdang"),
        new NetMall("taobao")
    );

    public static List<String> getPriceByStep(List<NetMall> list,String productName){
         return list.stream()
                    .map(netMall -> String.format(productName+"in %s price is %.2f",netMall.getNetMallName(),netMall.calcPrice(productName)))
                    .collect(Collectors.toList());
    }

    public static List<String> getPriceByCompletableFuture(List<NetMall> list,String productName){

        return list.stream().map(netMall -> CompletableFuture.supplyAsync(() -> 
            { return String.format(productName+"in %s price is %.2f",netMall.getNetMallName(),netMall.calcPrice(productName));}
            )).collect(Collectors.toList()).stream().map(s -> s.join()).collect(Collectors.toList());
        
    }
    
    public static void main(String[] args) {
        
        long startTime = System.currentTimeMillis();

        List<String> priceByStep = getPriceByStep(list, "mysql");
       for (String priceByStep2 : priceByStep) {
            System.out.println(priceByStep2);
       }
        
        long endTime = System.currentTimeMillis();
        System.out.println("use time is "+(endTime - startTime)+" ms");

        long startTime1 = System.currentTimeMillis();

        List<String> priceByCompletableFuture = getPriceByCompletableFuture(list, "mysql");
        for (String priceByCompletableFuture2 : priceByCompletableFuture) {
                System.out.println(priceByCompletableFuture2);
        }
        
        long endTime1 = System.currentTimeMillis();
        System.out.println("use time is "+(endTime1 - startTime1)+" ms");
    }

}

class NetMall{

    private String  netMallName;

    public NetMall(String netMallName) {
        this.netMallName = netMallName;
    }

    public String getNetMallName() {
        return netMallName;
    }

    public double calcPrice(String name){

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return ThreadLocalRandom.current().nextDouble() * 2 + name.charAt(0);
    }

}
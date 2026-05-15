package boardgames_shop.entity;

public enum OrderStatus {

    NOT_CREATED,    //Не оформлен
    CREATED,        //Оформлен
    IN_TRANSIT,     //В пути
    PICKUP_POINT,   //В пункте выдачи
    COMPLETED,      //Завершен
    CANCELLED       //Отменен
}
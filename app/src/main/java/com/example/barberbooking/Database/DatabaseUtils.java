package com.example.barberbooking.Database;

import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;

import com.example.barberbooking.Common.Common;
import com.example.barberbooking.Interface.ICartItemLoadListener;
import com.example.barberbooking.Interface.ICountItemInCartListener;
import com.example.barberbooking.Interface.ISumCartListener;

import java.util.List;

public class DatabaseUtils {

    public static void sumCart(CartDatabase db, ISumCartListener iSumCartListener)
    {
        SumCartAsync task = new SumCartAsync(db,iSumCartListener);
        task.execute();
    }

    public static void getAllCart(CartDatabase db, ICartItemLoadListener cartItemLoadListener)
    {
        GetAllCartAsync task = new GetAllCartAsync(db,cartItemLoadListener);
        task.execute();
    }

    public static void updateCart(CartDatabase db,CartItem cartItem)
    {
        UpdateCartAsync task = new UpdateCartAsync(db);
        task.execute(cartItem);
    }

    public static void insertToCart(CartDatabase db,CartItem...cartItems)
    {
        insertToCartAsync task = new insertToCartAsync(db);
        task.execute(cartItems);
    }

    public static void countItemInCart(CartDatabase db, ICountItemInCartListener iCountItemInCartListener)
    {
        CountItemInCartAsync task = new CountItemInCartAsync(db,iCountItemInCartListener);
        task.execute();
    }

    /* ====================================================================================================== */

    private static class SumCartAsync extends AsyncTask<Void,Void,Long>
    {
        private final CartDatabase db;
        private final ISumCartListener listener;

        public SumCartAsync(CartDatabase db, ISumCartListener listener) {
            this.db = db;
            this.listener = listener;
        }

        @Override
        protected Long doInBackground(Void... voids) {
            return db.cartDAO().sumPrice(Common.currentUser.getPhoneNumber());
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            listener.onSumCartSuccess(aLong);
        }
    }

    private static class UpdateCartAsync extends AsyncTask<CartItem,Void,Void>{

        private final CartDatabase db;

        public UpdateCartAsync(CartDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(CartItem... cartItems) {
            db.cartDAO().update(cartItems[0]);
            return null;
        }
    }

    private static class GetAllCartAsync extends AsyncTask<String,Void,List<CartItem>>{

        CartDatabase db;
        ICartItemLoadListener listener;
        public GetAllCartAsync(CartDatabase cartDatabase,ICartItemLoadListener cartItemLoadListener) {
            db = cartDatabase;
            listener = cartItemLoadListener;
        }

        @Override
        protected List<CartItem> doInBackground(String... strings) {
            return db.cartDAO().getAllItemFromCart(Common.currentUser.getPhoneNumber());
        }

        @Override
        protected void onPostExecute(List<CartItem> cartItems) {
            super.onPostExecute(cartItems);
            listener.onGetAllItemFromCartSuccess(cartItems);
        }
    }

    private static class insertToCartAsync extends AsyncTask<CartItem,Void,Void>{

        CartDatabase db;
        public insertToCartAsync(CartDatabase cartDatabase) {
            db = cartDatabase;
        }

        @Override
        protected Void doInBackground(CartItem... cartItems) {
            inserToCart(db,cartItems[0]);
            return null;
        }

        private void inserToCart(CartDatabase db, CartItem cartItem) {
            try{
                db.cartDAO().insert(cartItem);
            }catch (SQLiteConstraintException exception)
            {
                CartItem updateCartItem = db.cartDAO().getProductInCart(cartItem.getProductId(),
                        Common.currentUser.getPhoneNumber());
                updateCartItem.setProductQuantity(updateCartItem.getProductQuantity()+1);
                db.cartDAO().update(updateCartItem);
            }
        }
    }

    private static class CountItemInCartAsync extends AsyncTask<Void,Void,Integer>{

        CartDatabase db;
        ICountItemInCartListener listener;
        public CountItemInCartAsync(CartDatabase cartDatabase,ICountItemInCartListener iCountItemInCartListener) {
            db = cartDatabase;
            listener = iCountItemInCartListener;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return Integer.parseInt(String.valueOf(countItemInCartRun(db)));
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            listener.onCartItemCountSuccess(integer.intValue());
        }

        private int countItemInCartRun(CartDatabase db) {
            return db.cartDAO().countItemCart(Common.currentUser.getPhoneNumber());
        }
    }
}

package com.mymall.service.Impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mymall.common.Const;
import com.mymall.common.ServiceResponse;
import com.mymall.dao.CartMapper;
import com.mymall.dao.ProductMapper;
import com.mymall.pojo.Cart;
import com.mymall.pojo.Product;
import com.mymall.service.ICartService;
import com.mymall.util.BigDecimalUtil;
import com.mymall.util.PropertiesUtil;
import com.mymall.vo.CartProductVo;
import com.mymall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Service("iCartService")
public class CartServiceImp implements ICartService {
@Autowired
private CartMapper cartMapper;
@Autowired
private ProductMapper productMapper;
    @Override
    public ServiceResponse<CartVo> add(Integer userid,Integer count, Integer productid) {
        if (productid==null||count==null)
            return ServiceResponse.createByErrorMessage("参数错误");
        Cart cart = cartMapper.selectCartByUserIdProductId(userid, productid);//如果能查到购物车有了直接加count+=count
        //如果没查到添加商品到购物车
        if (cart == null) {
            Cart cartitem = new Cart();
            cartitem.setQuantity(count);
            cartitem.setProductId(productid);
            cartitem.setUserId(userid);
            int rowcount = cartMapper.insert(cartitem);
            if (rowcount < 0) {
                return ServiceResponse.createByErrorMessage("商品添加购物车失败");
            }
        } else {
            cart.setQuantity(cart.getQuantity() + count);
            int rowcount = cartMapper.updateByPrimaryKeySelective(cart);
            if (rowcount <0) {
                return ServiceResponse.createByErrorMessage("商品添加购物车失败");
            }
        }
        CartVo cartVo=this.getCartVoLimit(userid);
        return ServiceResponse.createBysuccessMessage(cartVo);
    }

    @Override
    public ServiceResponse update(Integer userid, Integer count, Integer productid) {
        if (productid==null||count==null)
            return ServiceResponse.createByErrorMessage("参数错误");
        //查找到此商品
        Cart cart=cartMapper.selectCartByUserIdProductId(userid,productid);
        if(cart!=null){
            cart.setQuantity(count);
        }else return ServiceResponse.createByErrorMessage("未查找到参数对应的信息");
        cartMapper.updateByPrimaryKey(cart);
        CartVo cartVo=this.getCartVoLimit(userid);
        return ServiceResponse.createBysuccessMessage(cartVo);
    }

    @Override
    public ServiceResponse deleteproduct(Integer userid, String productids) {
        //productids product的id其中由逗号分隔
        //处理  productids
        List<String> productList= Splitter.on(",").splitToList(productids);
        if(CollectionUtils.isNotEmpty(productList)){
            //如果数据有效 删除数据
            cartMapper.deleteproduct(userid,productList);
        }
        return null;
    }

    @Override
    public ServiceResponse list(Integer userid) {
        CartVo cartVo =this.getCartVoLimit(userid);
        return ServiceResponse.createBysuccessMessage(cartVo);
    }

    @Override
    public ServiceResponse selectAll(Integer userid,Integer checked,Integer productid) {
        //将用户的购物车全选
        cartMapper.checkOrUncheckedProduct(userid,checked,productid);
        return this.list(userid);
    }

    @Override
    public ServiceResponse<Integer> selectCartproductCount(Integer userid) {

        return ServiceResponse.createBysuccessMessage(cartMapper.selectCartproductCount(userid));
    }

    private CartVo getCartVoLimit(Integer userid){
        CartVo cartVo=new CartVo();
        List<Cart> cartList=cartMapper.selectCartByUserId(userid);
        List<CartProductVo> cartProductVoList= Lists.newArrayList();
        BigDecimal cartTotalPrice=new BigDecimal("0");
        CartProductVo cartProductVo=null;
        if(CollectionUtils.isNotEmpty(cartList)){
            for (Cart cart:cartList){
               Product product=productMapper.selectByPrimaryKey(cart.getProductId());
                if(product!=null) {
                   cartProductVo = assembleCartProductVo(cart, product);
                    int buylimitcount=0;
                    if(product.getStock()>=cart.getQuantity())//产品的库存大于购物车数量
                    {
                        //库存充足的时候
                        buylimitcount=product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buylimitcount=product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);//超出库存
                        //购物车更新有效库存
                        Cart cart1=new Cart();
                        cart1.setId(cart.getId());
                        cart1.setQuantity(buylimitcount);
                    }
                    cartProductVo.setQuantity(buylimitcount);
                    //计算总价
                       //1.产品总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(cartProductVo.getQuantity(),
                            cartProductVo.getProductPrice().doubleValue()));
                    //并勾选商品
                    cartProductVo.setProductChecked(cart.getChecked());
                }
                if(cart.getChecked()==Const.Cart.CHECKED){
                    //如果购物车已勾选计入总价
                    cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(),
                            cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllcheck(this.getAllCheckedStatus(userid));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return  cartVo;
    }
    private boolean getAllCheckedStatus(Integer userid){
        if(userid==null)
            return false;
        return  (cartMapper.selectCartProductCheckedStatusByUserId(userid)==0);
    }

    private CartProductVo assembleCartProductVo(Cart cart,Product product) {
        //cart 映射
        CartProductVo cartProductVo=new CartProductVo();
        cartProductVo.setId(cart.getId());
        cartProductVo.setUserId(cart.getUserId());
        cartProductVo.setProductId(cart.getProductId());
        //product 映射
        cartProductVo.setProductSubtitle(product.getSubtitle());
        cartProductVo.setProductMainImage(product.getMainImage());
        cartProductVo.setProductName(product.getName());
        cartProductVo.setProductStatus(product.getStatus());
        cartProductVo.setProductPrice(product.getPrice());
        cartProductVo.setProductStock(product.getStock());
        //判断库存

        return  cartProductVo;
    }

}

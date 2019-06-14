//package com.gmail.liuzechu2013.singapore.jars.Behavior;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.support.design.widget.CoordinatorLayout;
//import android.support.v4.view.ViewCompat;
//import android.support.v4.widget.NestedScrollView;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.gmail.liuzechu2013.singapore.jars.R;
//
//import edmt.dev.advancednestedscrollview.MaxHeightRecyclerView;
//import edmt.dev.advancednestedscrollview.MyViewGroupUtils;
//
//public class CustomBehavior extends CoordinatorLayout.Behavior<NestedScrollView> {
//
//    public CustomBehavior(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    @Override
//    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull NestedScrollView child, @NonNull View dependency) {
//        return dependency.getId() == R.id.profile_constraint_layout; // different from toolBar container in tutorial
//    }
//
//
//    @Override
//    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull NestedScrollView child, int layoutDirection) {
//
//        int fabHalfHeight = child.findViewById(R.id.fab).getHeight() / 2;
//        setTopMargin(child.findViewById(R.id.card_view), fabHalfHeight); // Margin Card View to 1/2 fab height
//        int rvMaxHeight = child.getHeight() - fabHalfHeight - child.findViewById(R.id.card_title).getHeight();
//
//        MaxHeightRecyclerView rv = child.findViewById(R.id.card_recycler_view);
//        rv.setMaxHeight(rvMaxHeight);
//
//        View cardContainer = child.findViewById(R.id.card_container);
//        // DID NOT IMPLEMENT OFFSET FOR TOP TOOL BAR as in https://www.youtube.com/watch?v=5jcem4EEODw
//        int toolBarContainerHeight = parent.getDependencies(child).get(0).getHeight();
//        setPaddingTop(cardContainer, rvMaxHeight - toolBarContainerHeight);
//        ViewCompat.offsetTopAndBottom(child, toolBarContainerHeight);
//        setPaddingBottom(rv, toolBarContainerHeight);
//
//        return true;
//    }
//
//    private void setPaddingBottom(View view, int bottom) {
//        if (view.getPaddingBottom() != bottom) {
//            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), bottom);
//        }
//    }
//
//    private void setPaddingTop(View view, int top) {
//        if (view.getPaddingTop() != top) {
//            view.setPadding(view.getPaddingLeft(), top, view.getPaddingRight(), view.getPaddingBottom());
//        }
//    }
//
//
//    @Override
//    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull NestedScrollView child, @NonNull MotionEvent ev) {
//        // block all touch events that originate within the bounds of our NestedScrollView but DO NOT
//        // originate from within the bounds of its inner CardView and Floating Action Button.
//
//        return ev.getActionMasked() == MotionEvent.ACTION_DOWN && isTouchInChildBounds(parent, child, ev)
//                && !isTouchInChildBounds(parent, child.findViewById(R.id.card_view), ev)
//                && !isTouchInChildBounds(parent, child.findViewById(R.id.fab), ev);
//    }
//
//    private boolean isTouchInChildBounds(ViewGroup parent, View child, MotionEvent ev) {
//        return MyViewGroupUtils.isPointInChildBounds(parent, child, (int)ev.getX(), (int)ev.getY());
//    }
//
//    private void setTopMargin(View view, int top) {
//        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
//        if (lp.topMargin != top) {
//            lp.topMargin = top;
//            view.setLayoutParams(lp);
//        }
//    }
//}

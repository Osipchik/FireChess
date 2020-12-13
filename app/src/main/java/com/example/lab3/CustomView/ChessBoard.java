package com.example.lab3.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.lab3.Enums.ChessColor;
import com.example.lab3.Models.ChessItem;
import com.example.lab3.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChessBoard extends View {
    private float originX = 0;
    private float originY = 0;
    private float cellSide = 130f;
    private final int lightColor;
    private final int darkColor;

    private final Map<Integer, Bitmap> bitmaps = new HashMap<>();
    private final Paint boardPaint = new Paint();
    private final Paint strokePaint = new Paint();

    private ChessItem selectedItem;

    public IChessDelegate chessDelegate;
    public ChessColor player;

    public interface IChessDelegate {
        ChessItem pieceAt(int col, int row);
        void moveTo(ChessItem piece, int col, int row);
    }

    public ChessBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ChessBoard, 0, 0);

        int activeColor;
        int strokeWidth;
        try {
            darkColor = a.getColor(R.styleable.ChessBoard_dark_color, Color.BLACK);
            lightColor = a.getColor(R.styleable.ChessBoard_light_color, Color.WHITE);
            activeColor = a.getColor(R.styleable.ChessBoard_select_color, Color.YELLOW);
            strokeWidth = a.getInteger(R.styleable.ChessBoard_stroke_width, 10);
        } finally {
            a.recycle();
        }

        strokePaint.setColor(activeColor);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(strokeWidth);

        loadBitmaps();
    }

    @Override
    public void onDraw(Canvas canvas) {
        float chessBoardSide = Math.min(getWidth(), getHeight());
        cellSide = chessBoardSide / 8f;
        originX = (getWidth() - chessBoardSide) / 2f;
        originY = (getHeight() - chessBoardSide) / 2f;

        drawChessboard(canvas);
        drawPieces(canvas);
        drawHighlightRectangle(canvas);
    }


    private void drawPieces(Canvas canvas) {
        for (int row = 0; row <= 7; row++) {
            for (int col = 0; col <= 7; col++) {
                ChessItem item = chessDelegate.pieceAt(col, row);
                if (item != null) {
                    drawPieceAt(canvas, col, row, item.resId);
                }
            }
        }
    }

    private void drawPieceAt(Canvas canvas, int col, int row, int resID) {
        Bitmap bitmap = bitmaps.get(resID);
        canvas.drawBitmap(bitmap, null, drawRect(col, row), boardPaint);
    }

    private void drawChessboard(Canvas canvas) {
        for (int row = 0; row <= 7; row++) {
            for (int col = 0; col <= 7; col++) {
                boardPaint.setColor(isDark(col, row) ? darkColor : lightColor);
                canvas.drawRect(drawRect(col, row), boardPaint);
            }
        }
    }

    private Rect drawRect(int col, int row){
        return new Rect((int) (originX + col * cellSide),
                (int) (originY + row * cellSide),
                (int) (originX + (col + 1) * cellSide),
                (int) (originY + (row + 1) * cellSide));
    }

    private void drawHighlightRectangle(Canvas canvas) {
        if (selectedItem != null) {
            canvas.drawRect(drawRect(selectedItem.col, selectedItem.row), strokePaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (!isEnabled()){
            return false;
        }

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                int currentCol = (int) ((x - originX) / cellSide);
                int currentRow = (int) ((y - originY) / cellSide);

                ChessItem piece = chessDelegate.pieceAt(currentCol, currentRow);
                if (piece != null) {
                    if (piece.player == player) {
                        selectedItem = piece;
                    }
                    else if (selectedItem != null) {
                        chessDelegate.moveTo(selectedItem, currentCol, currentRow);
                    }
                }
                else if (selectedItem != null) {
                    chessDelegate.moveTo(selectedItem, currentCol, currentRow);
                }

                invalidate();
            } break;
//            case MotionEvent.ACTION_MOVE: {
//
//                invalidate();
//            } break;
//            case MotionEvent.ACTION_UP: {
//
//                invalidate();
//            }
        }

        return true;
    }

    private boolean isDark(int col, int row){
        return (col + row) % 2 == 1;
    }

    private void loadBitmaps() {
        ArrayList<Integer> imgResIDs = new ArrayList<>(12);
        imgResIDs.add(R.drawable.bishop_black);
        imgResIDs.add(R.drawable.bishop_white);
        imgResIDs.add(R.drawable.king_black);
        imgResIDs.add(R.drawable.king_white);
        imgResIDs.add(R.drawable.queen_black);
        imgResIDs.add(R.drawable.queen_white);
        imgResIDs.add(R.drawable.rook_black);
        imgResIDs.add(R.drawable.rook_white);
        imgResIDs.add(R.drawable.knight_black);
        imgResIDs.add(R.drawable.knight_white);
        imgResIDs.add(R.drawable.pawn_black);
        imgResIDs.add(R.drawable.pawn_white);

        for (int i: imgResIDs){
            bitmaps.put(i, BitmapFactory.decodeResource(getResources(), i));
        }
    }
}
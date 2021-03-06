package com.example.umayado;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.bassaer.chatmessageview.models.Message;
import com.github.bassaer.chatmessageview.models.User;
import com.github.bassaer.chatmessageview.views.ChatView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    private SpeechRecognizer sr;
    private ChatView mChatView;

    // RecognitionListenerの定義
    // 中が空でも全てのメソッドを書く必要がある
    class listener implements RecognitionListener {
        // 話し始めたときに呼ばれる
        public void onBeginningOfSpeech() {
            /*Toast.makeText(getApplicationContext(), "onBeginningofSpeech",
                    Toast.LENGTH_SHORT).show();*/
        }

        // 結果に対する反応などで追加の音声が来たとき呼ばれる
        // しかし呼ばれる保証はないらしい
        public void onBufferReceived(byte[] buffer) {
        }

        // 話し終わった時に呼ばれる
        public void onEndOfSpeech() {
            /*Toast.makeText(getApplicationContext(), "onEndofSpeech",
                    Toast.LENGTH_SHORT).show();*/
        }

        // ネットワークエラーか認識エラーが起きた時に呼ばれる
        public void onError(int error) {
            String reason = "";
            boolean is_show = true;

            switch (error) {
                // Audio recording error
                case SpeechRecognizer.ERROR_AUDIO:
                    reason = "ERROR_AUDIO";
                    break;
                // Other client side errors
                case SpeechRecognizer.ERROR_CLIENT:
                    reason = "ERROR_CLIENT";
                    break;
                // Insufficient permissions
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    reason = "ERROR_INSUFFICIENT_PERMISSIONS";
                    break;
                // 	Other network related errors
                case SpeechRecognizer.ERROR_NETWORK:
                    reason = "ERROR_NETWORK";
                    /* ネットワーク接続をチェックする処理をここに入れる */
                    break;
                // Network operation timed out
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    reason = "ERROR_NETWORK_TIMEOUT";
                    break;
                // No recognition result matched
                case SpeechRecognizer.ERROR_NO_MATCH:
                    reason = "ERROR_NO_MATCH";
                    is_show = false;
                    break;
                // RecognitionService busy
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    reason = "ERROR_RECOGNIZER_BUSY";
                    break;
                // Server sends error status
                case SpeechRecognizer.ERROR_SERVER:
                    reason = "ERROR_SERVER";
                    /* ネットワーク接続をチェックをする処理をここに入れる */
                    break;
                // No speech input
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    reason = "ERROR_SPEECH_TIMEOUT";
                    is_show = false;
                    break;
                default:
                    is_show = false;
                    break;
            }
            if(is_show) {
                Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_SHORT).show();
            }
            restartListeningService();
        }

        // 将来の使用のために予約されている
        public void onEvent(int eventType, Bundle params) {
        }

        // 部分的な認識結果が利用出来るときに呼ばれる
        // 利用するにはインテントでEXTRA_PARTIAL_RESULTSを指定する必要がある
        public void onPartialResults(Bundle partialResults) {
            Log.d("My Test", "onPartialResults");
        }

        // 音声認識の準備ができた時に呼ばれる
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(), "音声認識中",
                    Toast.LENGTH_SHORT).show();
        }

        // 認識結果が準備できた時に呼ばれる
        public void onResults(Bundle results) {
            Log.d("My Test", "onResults");
            // 結果をArrayListとして取得
            ArrayList results_array = results.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION);
            // 取得した文字列を結合
            String resultsString = "";
            resultsString += results_array.get(0);

            // トーストを使って結果表示
            //Toast.makeText(getApplicationContext(), resultsString, Toast.LENGTH_LONG).show();

            //ChatViewに結果表示
            showChatView(resultsString);

            //Slackに結果送信
            //AsyncSlack task = new AsyncSlack();
            //task.execute(resultsString);

            restartListeningService();

        }

        // サウンドレベルが変わったときに呼ばれる
        // 呼ばれる保証はない
        public void onRmsChanged(float rmsdB) {
        }
    }

    // 音声認識を開始する
    protected void startListening() {
        try {
            if (sr == null) {
                sr = SpeechRecognizer.createSpeechRecognizer(this);
                if (!SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "音声認識が使えません",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                sr.setRecognitionListener(new listener());
            }
            // インテントの作成
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            // 言語モデル指定
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPAN.toString());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true); //オフライン認識を強制
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true); //部分フィードバックを有効化
            sr.startListening(intent);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "startListening()でエラーが起こりました",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // 音声認識を終了する
    protected void stopListening() {
        if (sr != null) sr.destroy();
        sr = null;
    }

    // 音声認識を再開する
    public void restartListeningService() {
        stopListening();
        startListening();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChatView = (ChatView) findViewById(R.id.chat_view);
        //Set UI parameters if you need
        mChatView.setLeftBubbleColor(ContextCompat.getColor(this, R.color.green500));
        mChatView.setRightBubbleColor(Color.WHITE);
        mChatView.setBackgroundColor(ContextCompat.getColor(this, R.color.blueGray500));
        mChatView.setSendButtonColor(ContextCompat.getColor(this, R.color.cyan900));
        mChatView.setSendIcon(R.drawable.ic_action_send);
        mChatView.setLeftMessageTextColor(Color.WHITE);
        mChatView.setRightMessageTextColor(Color.BLACK);
        mChatView.setUsernameTextColor(Color.WHITE);
        mChatView.setSendTimeTextColor(Color.WHITE);
        mChatView.setDateSeparatorColor(Color.WHITE);
        mChatView.setInputTextHint("new message...");
        mChatView.setMessageMarginTop(5);
        mChatView.setMessageMarginBottom(5);

        showChatView("Umayado Start !!");

        final User manual = new User(1, "Manual Input", null);
        //Click Send Button
        mChatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new message
                Message message = new Message.Builder()
                        .setUser(manual)
                        .setRightMessage(true)
                        .setMessageText(mChatView.getInputText())
                        .hideIcon(true)
                        .build();
                //Set to chat view
                mChatView.send(message);
                //Reset edit text
                mChatView.setInputText("");
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        startListening();
    }

    @Override
    protected void onPause() {
        stopListening();
        super.onPause();
    }

    private void showChatView(String text) {
        Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        final User me = new User(0, "UMAYADO", myIcon);
        Message message = new Message.Builder()
                .setUser(me)
                .setRightMessage(false)
                .setMessageText(text)
                .hideIcon(false)
                .build();
        //Set to chat view
        mChatView.send(message);
        //Reset edit text
        mChatView.setInputText("");
    }

}
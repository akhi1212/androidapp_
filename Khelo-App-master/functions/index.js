let functions = require('firebase-functions');
 
let admin = require('firebase-admin');
 
admin.initializeApp();
 
exports.sendNotification = functions.database.ref('/notifications/{userId}/{notificationId}').onWrite((change, context) => {
   
    //get the userId of the person receiving the notification because we need to get their token
    const receiverId = context.params.userId;
    console.log("receiverId: ", receiverId);
   
    //get the message
    const message = change.after.child('text').val();
    const type = change.after.child('type').val();
    console.log("message: ", message);
   
    //get the message id. We'll be sending this in the payload
    const messageId = context.params.notificationId;
    console.log("messageId: ", messageId);

    //get the token of the user receiving the message
    return admin.database().ref("/messageTokens/" + receiverId).once('value').then(snap => {
        const token = snap.val();
        console.log("token: ", token);
           
        //we have everything we need
        //Build the message payload and send the message
        console.log("Construction the notification message.");
        const payload = {
            data: {
                data_type: "direct_message",
                title: "New Message",
                type: type,
                message: message,
                message_id: messageId,
            }
        };
           
        return admin.messaging().sendToDevice(token, payload)
                    .then(function(response) {
                        console.log("Successfully sent message:", response);
                        return response.successCount;
                      })
                      .catch(function(error) {
                        console.log("Error sending message:", error);
                      });
        });
});
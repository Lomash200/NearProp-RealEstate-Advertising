document.addEventListener("DOMContentLoaded", function() {
    const payButton = document.getElementById("pay-button");
    const paymentInfo = document.getElementById("payment-info");
    
    // Razorpay test key
    const razorpayKeyId = "rzp_test_LoJiA2mTb0THiq";
    
    // Function to generate payment ID with the specified format
    function generatePaymentId() {
        const now = new Date();
        const year = now.getFullYear().toString().slice(-2); // Last 2 digits of year
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const date = String(now.getDate()).padStart(2, '0');
        const randomNum = Math.floor(Math.random() * 10000).toString().padStart(4, '0');
        
        return `RANPP${year}${month}${date}${randomNum}`;
    }
    
    payButton.addEventListener("click", function() {
        // Get form values
        const amount = parseFloat(document.getElementById("amount").value);
        const paymentType = document.getElementById("payment-type").value;
        const name = document.getElementById("name").value;
        const email = document.getElementById("email").value;
        const phone = document.getElementById("phone").value;
        
        if (!amount || amount <= 0 || !name || !email || !phone) {
            paymentInfo.innerHTML = '<p class="error">Please fill all fields correctly.</p>';
            return;
        }
        
        // Create a new payment ID
        const paymentId = generatePaymentId();
        
        // Configure Razorpay options
        const options = {
            key: razorpayKeyId,
            amount: amount * 100, // Razorpay expects amount in paise
            currency: "INR",
            name: "NearProp",
            description: `Payment for ${paymentType}`,
            image: "https://example.com/logo.png", // Replace with actual logo URL
            handler: function(response) {
                // Handle successful payment
                paymentInfo.innerHTML = `
                    <p class="success">Payment successful!</p>
                    <p><strong>Payment ID:</strong> ${paymentId}</p>
                    <p><strong>Razorpay Payment ID:</strong> ${response.razorpay_payment_id}</p>
                    <p><strong>Amount:</strong> ₹${amount}</p>
                    <p><strong>Type:</strong> ${paymentType}</p>
                `;
                
                // In a real implementation, you would send this data to your backend
                console.log("Payment success:", response);
            },
            prefill: {
                name: name,
                email: email,
                contact: phone
            },
            notes: {
                payment_type: paymentType,
                custom_payment_id: paymentId
            },
            theme: {
                color: "#3399cc"
            },
            modal: {
                ondismiss: function() {
                    paymentInfo.innerHTML = '<p>Payment cancelled by user</p>';
                }
            }
        };
        
        // Initialize Razorpay
        const rzp = new Razorpay(options);
        
        // Open Razorpay checkout
        rzp.open();
        
        // Display initial payment info
        paymentInfo.innerHTML = `
            <p>Payment initiated with ID: ${paymentId}</p>
            <p>Please complete the payment in the Razorpay window.</p>
        `;
        
        // Handle payment failure
        rzp.on('payment.failed', function(response) {
            paymentInfo.innerHTML = `
                <p class="error">Payment failed!</p>
                <p><strong>Error:</strong> ${response.error.description}</p>
                <p><strong>Payment ID:</strong> ${paymentId}</p>
            `;
            console.log("Payment failed:", response.error);
        });
    });
});

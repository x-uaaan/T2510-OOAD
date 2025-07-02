public class FeeCalculator {
    
    public static class FeeBreakdown {
        public double baseFee;
        public double cateringCost;
        public double transportCost;
        public double subtotal;
        public double earlyBirdDiscount;
        public double groupOrderDiscount;
        public double otherDiscount;
        public double totalDiscount;
        public double serviceTax;
        public double netPay;
        public String appliedVoucherDetails;
    }
    
    public static FeeBreakdown calculateFees(EventData event, int pax, boolean catering, boolean transport, String appliedVoucherCode) {
        FeeBreakdown breakdown = new FeeBreakdown();
        
        // Step 1: Calculate base costs
        breakdown.baseFee = event.getFee() * pax;
        breakdown.cateringCost = catering ? 20.0 * pax : 0;
        breakdown.transportCost = transport ? 10.0 * pax : 0;
        breakdown.subtotal = breakdown.baseFee + breakdown.cateringCost + breakdown.transportCost;
        
        // Step 2: Calculate discounts
        StringBuilder voucherDetails = new StringBuilder();
        breakdown.earlyBirdDiscount = 0;
        breakdown.groupOrderDiscount = 0;
        breakdown.otherDiscount = 0;
        
        // Event-based early bird discount
        if (event.isEarlyBirdEnabled() && event.getEarlyBirdEnd() != null) {
            long now = System.currentTimeMillis();
            if (now <= event.getEarlyBirdEnd().getTime()) {
                breakdown.earlyBirdDiscount = event.calculateEarlyBirdDiscount(breakdown.subtotal);
                if (breakdown.earlyBirdDiscount > 0) {
                    voucherDetails.append(String.format("• Early Bird: -RM %.2f\n", breakdown.earlyBirdDiscount));
                }
            }
        }
        
        // Process applied vouchers
        if (appliedVoucherCode != null && !appliedVoucherCode.trim().isEmpty()) {
            String[] voucherCodes = appliedVoucherCode.split(",");
            for (String code : voucherCodes) {
                code = code.trim();
                if (!code.isEmpty()) {
                    if (code.startsWith("EVENT_PROMO:")) {
                        // Event promo code
                        String eventPromoCode = code.substring("EVENT_PROMO:".length());
                        if (event.isPromoEnabled() && eventPromoCode.equals(event.getPromoCode())) {
                            double promoDiscount = event.calculatePromoDiscount(breakdown.subtotal);
                            breakdown.otherDiscount += promoDiscount;
                            voucherDetails.append(String.format("• %s: -RM %.2f\n", eventPromoCode, promoDiscount));
                        }
                    } else {
                        // Regular voucher
                        try {
                            VoucherData voucher = VoucherCSVManager.findVoucherByCode(code);
                            if (voucher != null && voucher.isApplicableFor(breakdown.subtotal, pax, event.getEventId(), "STUDENT")) {
                                double voucherDiscount = voucher.calculateDiscountAmount(breakdown.subtotal);
                                
                                switch (voucher.getVoucherType()) {
                                    case "EARLY_BIRD":
                                        breakdown.earlyBirdDiscount += voucherDiscount;
                                        voucherDetails.append(String.format("• %s: -RM %.2f\n", code, voucherDiscount));
                                        break;
                                    case "GROUP":
                                    case "GROUP_ORDER":
                                        breakdown.groupOrderDiscount += voucherDiscount;
                                        voucherDetails.append(String.format("• %s: -RM %.2f\n", code, voucherDiscount));
                                        break;
                                    default:
                                        breakdown.otherDiscount += voucherDiscount;
                                        voucherDetails.append(String.format("• %s: -RM %.2f\n", code, voucherDiscount));
                                        break;
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Error applying voucher " + code + ": " + e.getMessage());
                        }
                    }
                }
            }
        }
        
        breakdown.totalDiscount = breakdown.earlyBirdDiscount + breakdown.groupOrderDiscount + breakdown.otherDiscount;
        breakdown.appliedVoucherDetails = voucherDetails.toString();
        
        // Step 3: Calculate service tax on (subtotal - discount) as per user requirement
        double afterDiscount = breakdown.subtotal - breakdown.totalDiscount;
        if (afterDiscount < 0) afterDiscount = 0;
        breakdown.serviceTax = afterDiscount * 0.06;
        
        // Step 4: Calculate net pay
        breakdown.netPay = afterDiscount + breakdown.serviceTax;
        
        return breakdown;
    }
} 
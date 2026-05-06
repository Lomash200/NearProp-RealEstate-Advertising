--
-- PostgreSQL database dump
--

\restrict t7FlJXtBeVLH3Hq9HnbIcN5OMHHAiTG0Om6kBmwRzMI3b9pKlF3Lr59WPKeahjb

-- Dumped from database version 17.6
-- Dumped by pg_dump version 17.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: nearprop_user
--

-- *not* creating schema, since initdb creates it


ALTER SCHEMA public OWNER TO nearprop_user;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: advertisement_clicks; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.advertisement_clicks (
    id bigint NOT NULL,
    click_type character varying(255) NOT NULL,
    created_at timestamp(6) without time zone,
    ip_address character varying(255),
    referrer character varying(500),
    user_agent character varying(500),
    user_district character varying(255),
    user_id bigint,
    advertisement_id bigint NOT NULL,
    CONSTRAINT advertisement_clicks_click_type_check CHECK (((click_type)::text = ANY ((ARRAY['VIEW'::character varying, 'WEBSITE'::character varying, 'WHATSAPP'::character varying, 'PHONE'::character varying, 'INSTAGRAM'::character varying, 'FACEBOOK'::character varying, 'YOUTUBE'::character varying, 'TWITTER'::character varying, 'LINKEDIN'::character varying, 'OTHER'::character varying])::text[])))
);


ALTER TABLE public.advertisement_clicks OWNER TO nearprop_user;

--
-- Name: advertisement_clicks_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.advertisement_clicks_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.advertisement_clicks_id_seq OWNER TO nearprop_user;

--
-- Name: advertisement_clicks_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.advertisement_clicks_id_seq OWNED BY public.advertisement_clicks.id;


--
-- Name: advertisement_districts; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.advertisement_districts (
    advertisement_id bigint NOT NULL,
    district_id bigint NOT NULL
);


ALTER TABLE public.advertisement_districts OWNER TO nearprop_user;

--
-- Name: advertisements; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.advertisements (
    id bigint NOT NULL,
    active boolean NOT NULL,
    additional_info character varying(1000),
    banner_image_url character varying(255) NOT NULL,
    click_count bigint NOT NULL,
    created_at timestamp(6) without time zone,
    day_before_notification_sent boolean,
    description character varying(1000) NOT NULL,
    district_name character varying(255),
    email_address character varying(255),
    expiry_notification_sent boolean,
    facebook_url character varying(255),
    hours_before_notification_sent boolean,
    instagram_url character varying(255),
    latitude double precision,
    linkedin_url character varying(255),
    longitude double precision,
    phone_clicks bigint NOT NULL,
    phone_number character varying(255),
    radius_km double precision NOT NULL,
    social_media_clicks bigint NOT NULL,
    target_location character varying(255) NOT NULL,
    title character varying(255) NOT NULL,
    twitter_url character varying(255),
    updated_at timestamp(6) without time zone,
    valid_from timestamp(6) without time zone NOT NULL,
    valid_until timestamp(6) without time zone NOT NULL,
    video_url character varying(255),
    view_count bigint NOT NULL,
    website_clicks bigint NOT NULL,
    website_url character varying(255),
    whatsapp_clicks bigint NOT NULL,
    whatsapp_number character varying(255),
    youtube_url character varying(255),
    created_by bigint NOT NULL,
    district_id bigint
);


ALTER TABLE public.advertisements OWNER TO nearprop_user;

--
-- Name: advertisements_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.advertisements_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.advertisements_id_seq OWNER TO nearprop_user;

--
-- Name: advertisements_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.advertisements_id_seq OWNED BY public.advertisements.id;


--
-- Name: chat_attachments; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.chat_attachments (
    id bigint NOT NULL,
    content_type character varying(255) NOT NULL,
    created_at timestamp(6) without time zone,
    file_name character varying(255) NOT NULL,
    file_path character varying(255),
    file_size bigint NOT NULL,
    file_url character varying(255) NOT NULL,
    height integer,
    thumbnail_path character varying(255),
    thumbnail_url character varying(255),
    type character varying(255) NOT NULL,
    width integer,
    chat_room_id bigint,
    message_id bigint NOT NULL,
    uploader_id bigint,
    CONSTRAINT chat_attachments_type_check CHECK (((type)::text = ANY ((ARRAY['IMAGE'::character varying, 'DOCUMENT'::character varying, 'VIDEO'::character varying, 'AUDIO'::character varying])::text[])))
);


ALTER TABLE public.chat_attachments OWNER TO nearprop_user;

--
-- Name: chat_attachments_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.chat_attachments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.chat_attachments_id_seq OWNER TO nearprop_user;

--
-- Name: chat_attachments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.chat_attachments_id_seq OWNED BY public.chat_attachments.id;


--
-- Name: chat_messages; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.chat_messages (
    id bigint NOT NULL,
    content character varying(2000) NOT NULL,
    created_at timestamp(6) without time zone,
    delivered_at timestamp(6) without time zone,
    is_edited boolean,
    edited_at timestamp(6) without time zone,
    is_admin_message boolean,
    read_at timestamp(6) without time zone,
    is_reported boolean,
    status character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone,
    is_warned boolean,
    chat_room_id bigint NOT NULL,
    parent_message_id bigint,
    sender_id bigint NOT NULL,
    CONSTRAINT chat_messages_status_check CHECK (((status)::text = ANY ((ARRAY['SENT'::character varying, 'DELIVERED'::character varying, 'READ'::character varying])::text[])))
);


ALTER TABLE public.chat_messages OWNER TO nearprop_user;

--
-- Name: chat_messages_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.chat_messages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.chat_messages_id_seq OWNER TO nearprop_user;

--
-- Name: chat_messages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.chat_messages_id_seq OWNED BY public.chat_messages.id;


--
-- Name: chat_room_participants; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.chat_room_participants (
    chat_room_id bigint NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.chat_room_participants OWNER TO nearprop_user;

--
-- Name: chat_rooms; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.chat_rooms (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone,
    last_message_at timestamp(6) without time zone,
    status character varying(255),
    title character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone,
    buyer_id bigint NOT NULL,
    property_id bigint NOT NULL,
    seller_id bigint NOT NULL
);


ALTER TABLE public.chat_rooms OWNER TO nearprop_user;

--
-- Name: chat_rooms_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.chat_rooms_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.chat_rooms_id_seq OWNER TO nearprop_user;

--
-- Name: chat_rooms_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.chat_rooms_id_seq OWNED BY public.chat_rooms.id;


--
-- Name: coupons; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.coupons (
    id bigint NOT NULL,
    is_active boolean,
    code character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    current_uses integer,
    description character varying(255),
    discount_amount numeric(38,2),
    discount_percentage integer,
    discount_type character varying(255) NOT NULL,
    max_discount numeric(38,2),
    max_uses integer,
    permanent_id character varying(255) NOT NULL,
    subscription_type character varying(255),
    updated_at timestamp(6) without time zone,
    valid_from timestamp(6) without time zone NOT NULL,
    valid_until timestamp(6) without time zone NOT NULL,
    created_by bigint,
    CONSTRAINT coupons_discount_type_check CHECK (((discount_type)::text = ANY ((ARRAY['PERCENTAGE'::character varying, 'FIXED_AMOUNT'::character varying])::text[]))),
    CONSTRAINT coupons_subscription_type_check CHECK (((subscription_type)::text = ANY ((ARRAY['SELLER'::character varying, 'ADVISOR'::character varying, 'DEVELOPER'::character varying, 'FRANCHISEE'::character varying, 'PROPERTY'::character varying])::text[])))
);


ALTER TABLE public.coupons OWNER TO nearprop_user;

--
-- Name: coupons_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.coupons_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.coupons_id_seq OWNER TO nearprop_user;

--
-- Name: coupons_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.coupons_id_seq OWNED BY public.coupons.id;


--
-- Name: district_revenues; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.district_revenues (
    id bigint NOT NULL,
    amount numeric(38,2) NOT NULL,
    company_revenue numeric(38,2) NOT NULL,
    created_at timestamp(6) without time zone,
    description character varying(500),
    district_id bigint NOT NULL,
    district_name character varying(255) NOT NULL,
    franchisee_commission numeric(38,2) NOT NULL,
    payment_date timestamp(6) without time zone,
    payment_reference character varying(255),
    payment_status character varying(255) NOT NULL,
    property_id bigint,
    revenue_type character varying(255) NOT NULL,
    state character varying(255) NOT NULL,
    subscription_id bigint,
    transaction_date timestamp(6) without time zone NOT NULL,
    transaction_id character varying(255),
    updated_at timestamp(6) without time zone,
    franchisee_district_id bigint NOT NULL,
    CONSTRAINT district_revenues_payment_status_check CHECK (((payment_status)::text = ANY ((ARRAY['PENDING'::character varying, 'PAID'::character varying, 'CANCELLED'::character varying])::text[]))),
    CONSTRAINT district_revenues_revenue_type_check CHECK (((revenue_type)::text = ANY ((ARRAY['PROPERTY_LISTING'::character varying, 'SUBSCRIPTION_PAYMENT'::character varying, 'VISIT_BOOKING'::character varying, 'TRANSACTION_FEE'::character varying, 'MARKETING_FEE'::character varying, 'WITHDRAWAL'::character varying, 'OTHER'::character varying])::text[])))
);


ALTER TABLE public.district_revenues OWNER TO nearprop_user;

--
-- Name: district_revenues_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.district_revenues_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.district_revenues_id_seq OWNER TO nearprop_user;

--
-- Name: district_revenues_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.district_revenues_id_seq OWNED BY public.district_revenues.id;


--
-- Name: districts; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.districts (
    id bigint NOT NULL,
    active boolean NOT NULL,
    city character varying(255) NOT NULL,
    created_at timestamp(6) without time zone,
    latitude numeric(38,2),
    longitude numeric(38,2),
    name character varying(255) NOT NULL,
    pincode character varying(255),
    radius_km double precision,
    revenue_share_percentage numeric(38,2) NOT NULL,
    state character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone
);


ALTER TABLE public.districts OWNER TO nearprop_user;

--
-- Name: districts_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.districts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.districts_id_seq OWNER TO nearprop_user;

--
-- Name: districts_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.districts_id_seq OWNED BY public.districts.id;


--
-- Name: franchise_requests; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.franchise_requests (
    id bigint NOT NULL,
    aadhar_number character varying(255) NOT NULL,
    admin_comments character varying(1000),
    approved_at timestamp(6) without time zone,
    business_address character varying(255) NOT NULL,
    business_name character varying(255) NOT NULL,
    business_registration_number character varying(255),
    contact_email character varying(255) NOT NULL,
    contact_phone character varying(255) NOT NULL,
    created_at timestamp(6) without time zone,
    district_id bigint NOT NULL,
    district_name character varying(255) NOT NULL,
    document_ids character varying(1000),
    gst_number character varying(255),
    pan_number character varying(255) NOT NULL,
    reviewed_at timestamp(6) without time zone,
    reviewed_by bigint,
    state character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone,
    years_of_experience integer,
    user_id bigint NOT NULL,
    CONSTRAINT franchise_requests_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying])::text[])))
);


ALTER TABLE public.franchise_requests OWNER TO nearprop_user;

--
-- Name: franchise_requests_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.franchise_requests_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.franchise_requests_id_seq OWNER TO nearprop_user;

--
-- Name: franchise_requests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.franchise_requests_id_seq OWNED BY public.franchise_requests.id;


--
-- Name: franchisee_bank_details; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.franchisee_bank_details (
    id bigint NOT NULL,
    account_name character varying(255) NOT NULL,
    account_number character varying(255) NOT NULL,
    account_type character varying(255),
    bank_name character varying(255) NOT NULL,
    branch_name character varying(255),
    created_at timestamp(6) without time zone,
    ifsc_code character varying(255) NOT NULL,
    is_primary boolean NOT NULL,
    is_verified boolean NOT NULL,
    updated_at timestamp(6) without time zone,
    upi_id character varying(255),
    verified_at timestamp(6) without time zone,
    verified_by bigint,
    user_id bigint NOT NULL
);


ALTER TABLE public.franchisee_bank_details OWNER TO nearprop_user;

--
-- Name: franchisee_bank_details_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.franchisee_bank_details_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.franchisee_bank_details_id_seq OWNER TO nearprop_user;

--
-- Name: franchisee_bank_details_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.franchisee_bank_details_id_seq OWNED BY public.franchisee_bank_details.id;


--
-- Name: franchisee_districts; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.franchisee_districts (
    id bigint NOT NULL,
    active boolean NOT NULL,
    available_balance numeric(38,2),
    contact_email character varying(255),
    contact_phone character varying(255),
    created_at timestamp(6) without time zone,
    district_id bigint NOT NULL,
    district_name character varying(255) NOT NULL,
    end_date timestamp(6) without time zone,
    office_address character varying(255),
    revenue_share_percentage numeric(38,2) NOT NULL,
    start_date timestamp(6) without time zone NOT NULL,
    state character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    total_commission numeric(38,2),
    total_properties integer,
    total_revenue numeric(38,2),
    total_transactions integer,
    updated_at timestamp(6) without time zone,
    withdrawal_history text,
    franchise_request_id bigint,
    user_id bigint NOT NULL,
    CONSTRAINT franchisee_districts_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'SUSPENDED'::character varying, 'TERMINATED'::character varying, 'PENDING_APPROVAL'::character varying])::text[])))
);


ALTER TABLE public.franchisee_districts OWNER TO nearprop_user;

--
-- Name: franchisee_districts_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.franchisee_districts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.franchisee_districts_id_seq OWNER TO nearprop_user;

--
-- Name: franchisee_districts_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.franchisee_districts_id_seq OWNED BY public.franchisee_districts.id;


--
-- Name: franchisee_withdrawal_requests; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.franchisee_withdrawal_requests (
    id bigint NOT NULL,
    account_number character varying(255),
    admin_comments character varying(1000),
    bank_name character varying(255),
    created_at timestamp(6) without time zone,
    ifsc_code character varying(255),
    mobile_number character varying(255),
    original_balance numeric(38,2),
    payment_date timestamp(6) without time zone,
    payment_reference character varying(255),
    processed_at timestamp(6) without time zone,
    processed_by bigint,
    reason character varying(1000) NOT NULL,
    requested_amount numeric(38,2) NOT NULL,
    screenshot_url character varying(1000),
    status character varying(255) NOT NULL,
    transaction_id character varying(255),
    transaction_type character varying(255),
    updated_at timestamp(6) without time zone,
    updated_balance numeric(38,2),
    bank_detail_id bigint,
    franchisee_district_id bigint NOT NULL,
    CONSTRAINT franchisee_withdrawal_requests_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'APPROVED'::character varying, 'PAID'::character varying, 'REJECTED'::character varying])::text[])))
);


ALTER TABLE public.franchisee_withdrawal_requests OWNER TO nearprop_user;

--
-- Name: franchisee_withdrawal_requests_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.franchisee_withdrawal_requests_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.franchisee_withdrawal_requests_id_seq OWNER TO nearprop_user;

--
-- Name: franchisee_withdrawal_requests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.franchisee_withdrawal_requests_id_seq OWNED BY public.franchisee_withdrawal_requests.id;


--
-- Name: message_reports; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.message_reports (
    id bigint NOT NULL,
    admin_note character varying(1000),
    created_at timestamp(6) without time zone NOT NULL,
    description character varying(1000),
    processed_at timestamp(6) without time zone,
    reason character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    message_id bigint NOT NULL,
    processed_by_id bigint,
    reporter_id bigint NOT NULL
);


ALTER TABLE public.message_reports OWNER TO nearprop_user;

--
-- Name: message_reports_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.message_reports_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.message_reports_id_seq OWNER TO nearprop_user;

--
-- Name: message_reports_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.message_reports_id_seq OWNED BY public.message_reports.id;


--
-- Name: monthly_revenue_reports; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.monthly_revenue_reports (
    id bigint NOT NULL,
    account_name character varying(255),
    account_number character varying(255),
    admin_comments character varying(1000),
    admin_share numeric(38,2) NOT NULL,
    bank_name character varying(255),
    business_name character varying(255),
    current_balance numeric(38,2),
    district_id bigint NOT NULL,
    district_name character varying(255) NOT NULL,
    emergency_withdrawals_amount numeric(38,2),
    emergency_withdrawals_count integer,
    final_payable_amount numeric(38,2),
    franchisee_commission numeric(38,2) NOT NULL,
    franchisee_name character varying(255) NOT NULL,
    generated_at timestamp(6) without time zone NOT NULL,
    ifsc_code character varying(255),
    month integer NOT NULL,
    new_subscriptions integer,
    payment_date date,
    payment_due_date date,
    payment_method character varying(255),
    payment_reference character varying(255),
    previous_balance numeric(38,2),
    processed_at timestamp(6) without time zone,
    processed_by bigint,
    renewed_subscriptions integer,
    report_status character varying(255) NOT NULL,
    state character varying(255) NOT NULL,
    total_revenue numeric(38,2) NOT NULL,
    total_subscriptions integer,
    updated_at timestamp(6) without time zone,
    year integer NOT NULL,
    bank_detail_id bigint,
    franchisee_id bigint NOT NULL,
    franchisee_district_id bigint NOT NULL,
    CONSTRAINT monthly_revenue_reports_report_status_check CHECK (((report_status)::text = ANY ((ARRAY['PENDING'::character varying, 'PAID'::character varying, 'CANCELLED'::character varying])::text[])))
);


ALTER TABLE public.monthly_revenue_reports OWNER TO nearprop_user;

--
-- Name: monthly_revenue_reports_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.monthly_revenue_reports_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.monthly_revenue_reports_id_seq OWNER TO nearprop_user;

--
-- Name: monthly_revenue_reports_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.monthly_revenue_reports_id_seq OWNED BY public.monthly_revenue_reports.id;


--
-- Name: otps; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.otps (
    id bigint NOT NULL,
    attempts integer NOT NULL,
    blocked boolean NOT NULL,
    code character varying(255) NOT NULL,
    created_at timestamp(6) without time zone,
    email character varying(255),
    expires_at timestamp(6) without time zone,
    identifier character varying(255) NOT NULL,
    mobile_number character varying(255) NOT NULL,
    type character varying(255) NOT NULL,
    verified boolean NOT NULL,
    verified_at timestamp(6) without time zone,
    CONSTRAINT otps_type_check CHECK (((type)::text = ANY ((ARRAY['MOBILE'::character varying, 'EMAIL'::character varying])::text[])))
);


ALTER TABLE public.otps OWNER TO nearprop_user;

--
-- Name: otps_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.otps_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.otps_id_seq OWNER TO nearprop_user;

--
-- Name: otps_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.otps_id_seq OWNED BY public.otps.id;


--
-- Name: payment_transactions; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.payment_transactions (
    id bigint NOT NULL,
    amount numeric(38,2) NOT NULL,
    cancelled_at timestamp(6) without time zone,
    completed_at timestamp(6) without time zone,
    coupon_code character varying(255),
    created_at timestamp(6) without time zone NOT NULL,
    currency character varying(255) NOT NULL,
    discount_details character varying(255),
    failure_code character varying(255),
    failure_message character varying(255),
    gateway_order_id character varying(255),
    gateway_response text,
    gateway_transaction_id character varying(255),
    ip_address character varying(255),
    original_amount numeric(38,2),
    payment_date timestamp(6) without time zone,
    payment_id character varying(255),
    payment_method character varying(255),
    payment_type character varying(255) NOT NULL,
    property_id bigint,
    receipt_url character varying(255),
    reference_id character varying(255) NOT NULL,
    refund_amount numeric(38,2),
    refund_reason character varying(255),
    refund_reference_id character varying(255),
    refund_status character varying(255),
    status character varying(255) NOT NULL,
    subscription_id bigint,
    updated_at timestamp(6) without time zone NOT NULL,
    user_agent character varying(255),
    user_id bigint NOT NULL,
    CONSTRAINT payment_transactions_payment_method_check CHECK (((payment_method)::text = ANY ((ARRAY['CREDIT_CARD'::character varying, 'DEBIT_CARD'::character varying, 'NET_BANKING'::character varying, 'UPI'::character varying, 'WALLET'::character varying, 'BANK_TRANSFER'::character varying, 'EMI'::character varying, 'OTHER'::character varying])::text[]))),
    CONSTRAINT payment_transactions_payment_type_check CHECK (((payment_type)::text = ANY ((ARRAY['SUBSCRIPTION'::character varying, 'PROPERTY_LISTING'::character varying, 'FRANCHISE_FEE'::character varying, 'SERVICE_FEE'::character varying, 'REEL_PURCHASE'::character varying, 'OTHER'::character varying])::text[]))),
    CONSTRAINT payment_transactions_status_check CHECK (((status)::text = ANY ((ARRAY['INITIATED'::character varying, 'PROCESSING'::character varying, 'COMPLETED'::character varying, 'FAILED'::character varying, 'CANCELLED'::character varying, 'REFUNDED'::character varying, 'PARTIALLY_REFUNDED'::character varying])::text[])))
);


ALTER TABLE public.payment_transactions OWNER TO nearprop_user;

--
-- Name: payment_transactions_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.payment_transactions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.payment_transactions_id_seq OWNER TO nearprop_user;

--
-- Name: payment_transactions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.payment_transactions_id_seq OWNED BY public.payment_transactions.id;


--
-- Name: properties; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.properties (
    id bigint NOT NULL,
    active boolean NOT NULL,
    added_by_franchisee boolean NOT NULL,
    address character varying(255) NOT NULL,
    agreement_accepted boolean,
    approved boolean NOT NULL,
    area double precision,
    availability character varying(255),
    bathrooms integer,
    bedrooms integer,
    city character varying(255),
    created_at timestamp(6) without time zone,
    description character varying(1000) NOT NULL,
    district_name character varying(255) NOT NULL,
    district character varying(255) NOT NULL,
    featured boolean NOT NULL,
    garage_size double precision,
    garages integer,
    label character varying(255),
    land_area double precision,
    land_area_postfix character varying(255),
    latitude double precision,
    longitude double precision,
    note character varying(1000),
    owner_permanent_id character varying(255),
    permanent_id character varying(255),
    pincode character varying(255),
    place_name character varying(255),
    price numeric(38,2) NOT NULL,
    private_note character varying(1000),
    renovated character varying(255),
    scheduled_deletion timestamp(6) without time zone,
    size_postfix character varying(255),
    state character varying(255),
    status character varying(255) NOT NULL,
    stock integer,
    street_number character varying(255),
    subscription_expiry timestamp(6) without time zone,
    subscription_id bigint,
    title character varying(255) NOT NULL,
    type character varying(255) NOT NULL,
    unit_count integer,
    unit_type character varying(255),
    updated_at timestamp(6) without time zone,
    video_url character varying(255),
    year_built integer,
    youtube_url character varying(255),
    added_by_user_id bigint,
    district_id bigint,
    owner_id bigint NOT NULL,
    expiry_date timestamp(6) without time zone,
    CONSTRAINT properties_label_check CHECK (((label)::text = ANY ((ARRAY['GOLDEN_OFFER'::character varying, 'HOT_OFFER'::character varying, 'OPEN_HOUSE'::character varying, 'SOLD'::character varying, 'DEVELOPER'::character varying])::text[]))),
    CONSTRAINT properties_status_check CHECK (((status)::text = ANY ((ARRAY['AVAILABLE'::character varying, 'SOLD'::character varying, 'RENTED'::character varying, 'UNDER_CONTRACT'::character varying, 'PENDING_APPROVAL'::character varying, 'INACTIVE'::character varying, 'FOR_RENT'::character varying, 'FOR_SALE'::character varying, 'BLOCKED'::character varying, 'ACTIVE'::character varying, 'PENDING'::character varying, 'DELETED'::character varying])::text[]))),
    CONSTRAINT properties_type_check CHECK (((type)::text = ANY ((ARRAY['APARTMENT'::character varying, 'VILLA'::character varying, 'HOUSE'::character varying, 'PLOT'::character varying, 'COMMERCIAL'::character varying, 'OFFICE_SPACE'::character varying, 'SHOP'::character varying, 'WAREHOUSE'::character varying, 'FARMLAND'::character varying, 'PG_HOSTEL'::character varying, 'CONDO'::character varying, 'MULTI_FAMILY_HOME'::character varying, 'SINGLE_FAMILY_HOME'::character varying, 'STUDIO'::character varying, 'LAND'::character varying])::text[])))
);


ALTER TABLE public.properties OWNER TO nearprop_user;

--
-- Name: properties_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.properties_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.properties_id_seq OWNER TO nearprop_user;

--
-- Name: properties_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.properties_id_seq OWNED BY public.properties.id;


--
-- Name: property_additional_details; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.property_additional_details (
    property_id bigint NOT NULL,
    value character varying(255),
    title character varying(255) NOT NULL
);


ALTER TABLE public.property_additional_details OWNER TO nearprop_user;

--
-- Name: property_amenities; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.property_amenities (
    property_id bigint NOT NULL,
    amenity character varying(255)
);


ALTER TABLE public.property_amenities OWNER TO nearprop_user;

--
-- Name: property_features; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.property_features (
    property_id bigint NOT NULL,
    feature character varying(255)
);


ALTER TABLE public.property_features OWNER TO nearprop_user;

--
-- Name: property_images; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.property_images (
    property_id bigint NOT NULL,
    image_url character varying(255),
    image_order integer NOT NULL
);


ALTER TABLE public.property_images OWNER TO nearprop_user;

--
-- Name: property_inquiries; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.property_inquiries (
    id bigint NOT NULL,
    area character varying(255),
    bathrooms integer,
    bedrooms integer,
    city character varying(255),
    created_at timestamp(6) without time zone,
    district_id bigint,
    email character varying(255),
    info_type character varying(255),
    last_updated_at timestamp(6) without time zone,
    latitude double precision,
    longitude double precision,
    max_price double precision,
    message character varying(2000),
    min_size character varying(255),
    mobile_number character varying(255),
    name character varying(255),
    property_type character varying(255),
    state character varying(255),
    status character varying(255),
    zip_code character varying(255),
    CONSTRAINT property_inquiries_info_type_check CHECK (((info_type)::text = ANY ((ARRAY['RENT'::character varying, 'SELL'::character varying, 'PURCHASE'::character varying, 'OTHER'::character varying])::text[]))),
    CONSTRAINT property_inquiries_property_type_check CHECK (((property_type)::text = ANY ((ARRAY['APARTMENT'::character varying, 'VILLA'::character varying, 'HOUSE'::character varying, 'PLOT'::character varying, 'COMMERCIAL'::character varying, 'OFFICE_SPACE'::character varying, 'SHOP'::character varying, 'WAREHOUSE'::character varying, 'FARMLAND'::character varying, 'PG_HOSTEL'::character varying, 'CONDO'::character varying, 'MULTI_FAMILY_HOME'::character varying, 'SINGLE_FAMILY_HOME'::character varying, 'STUDIO'::character varying, 'LAND'::character varying])::text[]))),
    CONSTRAINT property_inquiries_status_check CHECK (((status)::text = ANY ((ARRAY['IN_REVIEW'::character varying, 'COMPLETED'::character varying, 'OTHER'::character varying])::text[])))
);


ALTER TABLE public.property_inquiries OWNER TO nearprop_user;

--
-- Name: property_inquiries_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.property_inquiries_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.property_inquiries_id_seq OWNER TO nearprop_user;

--
-- Name: property_inquiries_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.property_inquiries_id_seq OWNED BY public.property_inquiries.id;


--
-- Name: property_inquiry_status_history; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.property_inquiry_status_history (
    id bigint NOT NULL,
    comment character varying(1000),
    status character varying(255),
    updated_at timestamp(6) without time zone,
    updated_by bigint,
    inquiry_id bigint NOT NULL,
    CONSTRAINT property_inquiry_status_history_status_check CHECK (((status)::text = ANY ((ARRAY['IN_REVIEW'::character varying, 'COMPLETED'::character varying, 'OTHER'::character varying])::text[])))
);


ALTER TABLE public.property_inquiry_status_history OWNER TO nearprop_user;

--
-- Name: property_inquiry_status_history_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.property_inquiry_status_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.property_inquiry_status_history_id_seq OWNER TO nearprop_user;

--
-- Name: property_inquiry_status_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.property_inquiry_status_history_id_seq OWNED BY public.property_inquiry_status_history.id;


--
-- Name: property_luxurious_features; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.property_luxurious_features (
    property_id bigint NOT NULL,
    luxurious_feature character varying(255)
);


ALTER TABLE public.property_luxurious_features OWNER TO nearprop_user;

--
-- Name: property_reels; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.property_reels (
    id bigint NOT NULL,
    city character varying(255),
    comment_count bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    description character varying(500),
    district character varying(255),
    duration_seconds integer NOT NULL,
    file_size bigint NOT NULL,
    latitude double precision,
    like_count bigint NOT NULL,
    longitude double precision,
    payment_required boolean NOT NULL,
    payment_transaction_id character varying(255),
    processing_status character varying(255) NOT NULL,
    public_id character varying(255) NOT NULL,
    save_count bigint NOT NULL,
    share_count bigint NOT NULL,
    state character varying(255),
    status character varying(255) NOT NULL,
    thumbnail_url character varying(255) NOT NULL,
    title character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    video_url character varying(255) NOT NULL,
    view_count bigint NOT NULL,
    user_id bigint NOT NULL,
    property_id bigint NOT NULL,
    CONSTRAINT property_reels_processing_status_check CHECK (((processing_status)::text = ANY ((ARRAY['QUEUED'::character varying, 'PROCESSING'::character varying, 'COMPLETED'::character varying, 'FAILED'::character varying])::text[]))),
    CONSTRAINT property_reels_status_check CHECK (((status)::text = ANY ((ARRAY['DRAFT'::character varying, 'PUBLISHED'::character varying, 'ARCHIVED'::character varying, 'REJECTED'::character varying, 'HIDDEN'::character varying])::text[])))
);


ALTER TABLE public.property_reels OWNER TO nearprop_user;

--
-- Name: property_reels_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.property_reels_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.property_reels_id_seq OWNER TO nearprop_user;

--
-- Name: property_reels_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.property_reels_id_seq OWNED BY public.property_reels.id;


--
-- Name: property_reviews; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.property_reviews (
    id bigint NOT NULL,
    comment character varying(1000),
    created_at timestamp(6) without time zone,
    rating integer NOT NULL,
    updated_at timestamp(6) without time zone,
    property_id bigint NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.property_reviews OWNER TO nearprop_user;

--
-- Name: property_reviews_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.property_reviews_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.property_reviews_id_seq OWNER TO nearprop_user;

--
-- Name: property_reviews_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.property_reviews_id_seq OWNED BY public.property_reviews.id;


--
-- Name: property_security_features; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.property_security_features (
    property_id bigint NOT NULL,
    security_feature character varying(255)
);


ALTER TABLE public.property_security_features OWNER TO nearprop_user;

--
-- Name: property_update_fields; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.property_update_fields (
    update_request_id bigint NOT NULL,
    old_value text,
    field_name character varying(255) NOT NULL
);


ALTER TABLE public.property_update_fields OWNER TO nearprop_user;

--
-- Name: property_update_new_fields; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.property_update_new_fields (
    update_request_id bigint NOT NULL,
    new_value text,
    field_name character varying(255) NOT NULL
);


ALTER TABLE public.property_update_new_fields OWNER TO nearprop_user;

--
-- Name: property_update_requests; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.property_update_requests (
    id bigint NOT NULL,
    admin_approved boolean,
    admin_notes character varying(1000),
    admin_reviewed boolean,
    admin_reviewed_at timestamp(6) without time zone,
    district character varying(255),
    franchisee_approved boolean,
    franchisee_notes character varying(1000),
    is_franchisee_request boolean,
    franchisee_reviewed boolean,
    franchisee_reviewed_at timestamp(6) without time zone,
    rejection_reason character varying(1000),
    request_id character varying(255),
    request_notes character varying(1000),
    status character varying(255) NOT NULL,
    submitted_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    franchisee_id bigint,
    property_id bigint NOT NULL,
    requested_by bigint NOT NULL,
    reviewed_by_admin bigint,
    reviewed_by_franchisee bigint,
    CONSTRAINT property_update_requests_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying, 'CANCELLED'::character varying])::text[])))
);


ALTER TABLE public.property_update_requests OWNER TO nearprop_user;

--
-- Name: property_update_requests_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.property_update_requests_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.property_update_requests_id_seq OWNER TO nearprop_user;

--
-- Name: property_update_requests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.property_update_requests_id_seq OWNED BY public.property_update_requests.id;


--
-- Name: property_views; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.property_views (
    id bigint NOT NULL,
    viewed_at timestamp(6) without time zone,
    property_id bigint NOT NULL,
    user_id bigint
);


ALTER TABLE public.property_views OWNER TO postgres;

--
-- Name: property_views_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.property_views_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.property_views_id_seq OWNER TO postgres;

--
-- Name: property_views_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.property_views_id_seq OWNED BY public.property_views.id;


--
-- Name: property_visits; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.property_visits (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone,
    notes character varying(500),
    scheduled_time timestamp(6) without time zone NOT NULL,
    status character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone,
    property_id bigint NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT property_visits_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'CONFIRMED'::character varying, 'COMPLETED'::character varying, 'CANCELLED'::character varying, 'RESCHEDULED'::character varying])::text[])))
);


ALTER TABLE public.property_visits OWNER TO nearprop_user;

--
-- Name: property_visits_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.property_visits_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.property_visits_id_seq OWNER TO nearprop_user;

--
-- Name: property_visits_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.property_visits_id_seq OWNED BY public.property_visits.id;


--
-- Name: recent_viewed_properties; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.recent_viewed_properties (
    id bigint NOT NULL,
    viewed_at timestamp(6) without time zone,
    property_id bigint,
    user_id bigint
);


ALTER TABLE public.recent_viewed_properties OWNER TO postgres;

--
-- Name: recent_viewed_properties_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.recent_viewed_properties_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.recent_viewed_properties_id_seq OWNER TO postgres;

--
-- Name: recent_viewed_properties_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.recent_viewed_properties_id_seq OWNED BY public.recent_viewed_properties.id;


--
-- Name: reel_interactions; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.reel_interactions (
    id bigint NOT NULL,
    comment character varying(1000),
    created_at timestamp(6) without time zone NOT NULL,
    type character varying(255) NOT NULL,
    reel_id bigint NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT reel_interactions_type_check CHECK (((type)::text = ANY ((ARRAY['LIKE'::character varying, 'COMMENT'::character varying, 'FOLLOW'::character varying, 'VIEW'::character varying, 'SHARE'::character varying, 'SAVE'::character varying])::text[])))
);


ALTER TABLE public.reel_interactions OWNER TO nearprop_user;

--
-- Name: reel_interactions_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.reel_interactions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.reel_interactions_id_seq OWNER TO nearprop_user;

--
-- Name: reel_interactions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.reel_interactions_id_seq OWNED BY public.reel_interactions.id;


--
-- Name: review_likes; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.review_likes (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone,
    review_id bigint NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.review_likes OWNER TO nearprop_user;

--
-- Name: review_likes_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.review_likes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.review_likes_id_seq OWNER TO nearprop_user;

--
-- Name: review_likes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.review_likes_id_seq OWNED BY public.review_likes.id;


--
-- Name: role_request_documents; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.role_request_documents (
    role_request_id bigint NOT NULL,
    document_url character varying(255)
);


ALTER TABLE public.role_request_documents OWNER TO nearprop_user;

--
-- Name: role_requests; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.role_requests (
    id bigint NOT NULL,
    admin_comment character varying(255),
    comment text,
    created_at timestamp(6) without time zone,
    processed_at timestamp(6) without time zone,
    reason text,
    requested_role character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone,
    processed_by bigint,
    user_id bigint NOT NULL,
    CONSTRAINT role_requests_requested_role_check CHECK (((requested_role)::text = ANY (ARRAY[('SELLER'::character varying)::text, ('ADVISOR'::character varying)::text, ('DEVELOPER'::character varying)::text, ('ADMIN'::character varying)::text, ('SUBADMIN'::character varying)::text, ('FRANCHISEE'::character varying)::text]))),
    CONSTRAINT role_requests_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying])::text[])))
);


ALTER TABLE public.role_requests OWNER TO nearprop_user;

--
-- Name: role_requests_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.role_requests_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.role_requests_id_seq OWNER TO nearprop_user;

--
-- Name: role_requests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.role_requests_id_seq OWNED BY public.role_requests.id;


--
-- Name: sub_admin_permission; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sub_admin_permission (
    id bigint NOT NULL,
    action character varying(255),
    module character varying(255),
    subadmin_id bigint NOT NULL,
    CONSTRAINT sub_admin_permission_action_check CHECK (((action)::text = ANY ((ARRAY['VIEW'::character varying, 'CREATE'::character varying, 'UPDATE'::character varying, 'DELETE'::character varying])::text[]))),
    CONSTRAINT sub_admin_permission_module_check CHECK (((module)::text = ANY (ARRAY[('PROPERTY'::character varying)::text, ('ADVERTISEMENT'::character varying)::text, ('FRANCHISEE'::character varying)::text, ('SUBSCRIPTION'::character varying)::text, ('WITHDRAW'::character varying)::text])))
);


ALTER TABLE public.sub_admin_permission OWNER TO postgres;

--
-- Name: sub_admin_permission_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sub_admin_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sub_admin_permission_id_seq OWNER TO postgres;

--
-- Name: sub_admin_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.sub_admin_permission_id_seq OWNED BY public.sub_admin_permission.id;


--
-- Name: subscription_plan_features; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.subscription_plan_features (
    id bigint NOT NULL,
    allowed_video_formats character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    is_active boolean NOT NULL,
    max_properties integer NOT NULL,
    max_reel_duration_seconds integer NOT NULL,
    max_reel_file_size_mb integer NOT NULL,
    max_reels_per_property integer NOT NULL,
    max_total_reels integer NOT NULL,
    monthly_price double precision NOT NULL,
    plan_name character varying(255) NOT NULL,
    plan_type character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    CONSTRAINT subscription_plan_features_plan_type_check CHECK (((plan_type)::text = ANY ((ARRAY['USER'::character varying, 'SELLER'::character varying, 'ADVISOR'::character varying, 'DEVELOPER'::character varying, 'FRANCHISEE'::character varying])::text[])))
);


ALTER TABLE public.subscription_plan_features OWNER TO nearprop_user;

--
-- Name: subscription_plan_features_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.subscription_plan_features_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.subscription_plan_features_id_seq OWNER TO nearprop_user;

--
-- Name: subscription_plan_features_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.subscription_plan_features_id_seq OWNED BY public.subscription_plan_features.id;


--
-- Name: subscription_plans; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.subscription_plans (
    id bigint NOT NULL,
    active boolean NOT NULL,
    content_delete_after_days integer NOT NULL,
    content_hide_after_days integer NOT NULL,
    created_at timestamp(6) without time zone,
    description character varying(255) NOT NULL,
    duration_days integer NOT NULL,
    marketing_fee numeric(38,2),
    max_properties integer,
    max_reels_per_property integer,
    max_total_reels integer,
    name character varying(255) NOT NULL,
    price numeric(38,2) NOT NULL,
    type character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone,
    CONSTRAINT subscription_plans_type_check CHECK (((type)::text = ANY ((ARRAY['SELLER'::character varying, 'ADVISOR'::character varying, 'DEVELOPER'::character varying, 'FRANCHISEE'::character varying, 'PROPERTY'::character varying])::text[])))
);


ALTER TABLE public.subscription_plans OWNER TO nearprop_user;

--
-- Name: subscription_plans_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.subscription_plans_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.subscription_plans_id_seq OWNER TO nearprop_user;

--
-- Name: subscription_plans_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.subscription_plans_id_seq OWNED BY public.subscription_plans.id;


--
-- Name: subscriptions; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.subscriptions (
    id bigint NOT NULL,
    auto_renew boolean,
    cancelled_at timestamp(6) without time zone,
    content_deleted_at timestamp(6) without time zone,
    content_hidden_at timestamp(6) without time zone,
    coupon_code character varying(255),
    created_at timestamp(6) without time zone,
    discount_amount numeric(38,2),
    district_id bigint,
    end_date timestamp(6) without time zone NOT NULL,
    is_renewal boolean,
    marketing_fee numeric(38,2),
    original_price numeric(38,2),
    payment_confirmed boolean,
    payment_reference character varying(255),
    previous_subscription_id bigint,
    price numeric(38,2) NOT NULL,
    start_date timestamp(6) without time zone NOT NULL,
    status character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone,
    coupon_id bigint,
    plan_id bigint NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT subscriptions_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING_PAYMENT'::character varying, 'ACTIVE'::character varying, 'EXPIRED'::character varying, 'CANCELLED'::character varying, 'CONTENT_HIDDEN'::character varying, 'CONTENT_DELETED'::character varying])::text[])))
);


ALTER TABLE public.subscriptions OWNER TO nearprop_user;

--
-- Name: subscriptions_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.subscriptions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.subscriptions_id_seq OWNER TO nearprop_user;

--
-- Name: subscriptions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.subscriptions_id_seq OWNED BY public.subscriptions.id;


--
-- Name: user_favorites; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.user_favorites (
    user_id bigint NOT NULL,
    property_id bigint NOT NULL
);


ALTER TABLE public.user_favorites OWNER TO nearprop_user;

--
-- Name: user_following; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.user_following (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    followed_id bigint NOT NULL,
    follower_id bigint NOT NULL
);


ALTER TABLE public.user_following OWNER TO nearprop_user;

--
-- Name: user_following_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.user_following_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_following_id_seq OWNER TO nearprop_user;

--
-- Name: user_following_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.user_following_id_seq OWNED BY public.user_following.id;


--
-- Name: user_preferences; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.user_preferences (
    user_id bigint NOT NULL,
    app_notifications boolean,
    created_at timestamp(6) without time zone,
    currency character varying(255),
    dashboard_view character varying(255),
    date_format character varying(255),
    distance_unit character varying(255),
    email_notifications boolean,
    language character varying(255),
    property_view_type character varying(255),
    sms_notifications boolean,
    temperature_unit character varying(255),
    theme character varying(255),
    time_format character varying(255),
    updated_at timestamp(6) without time zone
);


ALTER TABLE public.user_preferences OWNER TO nearprop_user;

--
-- Name: user_roles; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.user_roles (
    user_id bigint NOT NULL,
    role character varying(255),
    CONSTRAINT user_roles_role_check CHECK (((role)::text = ANY (ARRAY['USER'::text, 'SELLER'::text, 'ADVISOR'::text, 'DEVELOPER'::text, 'FRANCHISEE'::text, 'ADMIN'::text, 'SUBADMIN'::text])))
);


ALTER TABLE public.user_roles OWNER TO nearprop_user;

--
-- Name: user_sessions; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.user_sessions (
    id bigint NOT NULL,
    active boolean NOT NULL,
    created_at timestamp(6) without time zone,
    device_info character varying(255) NOT NULL,
    expires_at timestamp(6) without time zone,
    ip_address character varying(255) NOT NULL,
    last_accessed_at timestamp(6) without time zone,
    session_id character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone,
    user_id bigint NOT NULL
);


ALTER TABLE public.user_sessions OWNER TO nearprop_user;

--
-- Name: user_sessions_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.user_sessions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_sessions_id_seq OWNER TO nearprop_user;

--
-- Name: user_sessions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.user_sessions_id_seq OWNED BY public.user_sessions.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    aadhaar_document_url character varying(512),
    aadhaar_number character varying(255),
    aadhaar_verified boolean NOT NULL,
    address character varying(255),
    created_at timestamp(6) without time zone,
    district character varying(255),
    district_id bigint,
    email character varying(255),
    email_verified boolean NOT NULL,
    last_login_at timestamp(6) without time zone,
    latitude double precision,
    longitude double precision,
    mobile_number character varying(255) NOT NULL,
    mobile_verified boolean NOT NULL,
    name character varying(255) NOT NULL,
    password character varying(128),
    permanent_id character varying(255),
    profile_image_url character varying(512),
    updated_at timestamp(6) without time zone
);


ALTER TABLE public.users OWNER TO nearprop_user;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO nearprop_user;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: advertisement_clicks id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.advertisement_clicks ALTER COLUMN id SET DEFAULT nextval('public.advertisement_clicks_id_seq'::regclass);


--
-- Name: advertisements id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.advertisements ALTER COLUMN id SET DEFAULT nextval('public.advertisements_id_seq'::regclass);


--
-- Name: chat_attachments id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_attachments ALTER COLUMN id SET DEFAULT nextval('public.chat_attachments_id_seq'::regclass);


--
-- Name: chat_messages id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_messages ALTER COLUMN id SET DEFAULT nextval('public.chat_messages_id_seq'::regclass);


--
-- Name: chat_rooms id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_rooms ALTER COLUMN id SET DEFAULT nextval('public.chat_rooms_id_seq'::regclass);


--
-- Name: coupons id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.coupons ALTER COLUMN id SET DEFAULT nextval('public.coupons_id_seq'::regclass);


--
-- Name: district_revenues id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.district_revenues ALTER COLUMN id SET DEFAULT nextval('public.district_revenues_id_seq'::regclass);


--
-- Name: districts id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.districts ALTER COLUMN id SET DEFAULT nextval('public.districts_id_seq'::regclass);


--
-- Name: franchise_requests id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.franchise_requests ALTER COLUMN id SET DEFAULT nextval('public.franchise_requests_id_seq'::regclass);


--
-- Name: franchisee_bank_details id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.franchisee_bank_details ALTER COLUMN id SET DEFAULT nextval('public.franchisee_bank_details_id_seq'::regclass);


--
-- Name: franchisee_districts id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.franchisee_districts ALTER COLUMN id SET DEFAULT nextval('public.franchisee_districts_id_seq'::regclass);


--
-- Name: franchisee_withdrawal_requests id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.franchisee_withdrawal_requests ALTER COLUMN id SET DEFAULT nextval('public.franchisee_withdrawal_requests_id_seq'::regclass);


--
-- Name: message_reports id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.message_reports ALTER COLUMN id SET DEFAULT nextval('public.message_reports_id_seq'::regclass);


--
-- Name: monthly_revenue_reports id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.monthly_revenue_reports ALTER COLUMN id SET DEFAULT nextval('public.monthly_revenue_reports_id_seq'::regclass);


--
-- Name: otps id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.otps ALTER COLUMN id SET DEFAULT nextval('public.otps_id_seq'::regclass);


--
-- Name: payment_transactions id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.payment_transactions ALTER COLUMN id SET DEFAULT nextval('public.payment_transactions_id_seq'::regclass);


--
-- Name: properties id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.properties ALTER COLUMN id SET DEFAULT nextval('public.properties_id_seq'::regclass);


--
-- Name: property_inquiries id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_inquiries ALTER COLUMN id SET DEFAULT nextval('public.property_inquiries_id_seq'::regclass);


--
-- Name: property_inquiry_status_history id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_inquiry_status_history ALTER COLUMN id SET DEFAULT nextval('public.property_inquiry_status_history_id_seq'::regclass);


--
-- Name: property_reels id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_reels ALTER COLUMN id SET DEFAULT nextval('public.property_reels_id_seq'::regclass);


--
-- Name: property_reviews id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_reviews ALTER COLUMN id SET DEFAULT nextval('public.property_reviews_id_seq'::regclass);


--
-- Name: property_update_requests id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_update_requests ALTER COLUMN id SET DEFAULT nextval('public.property_update_requests_id_seq'::regclass);


--
-- Name: property_views id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.property_views ALTER COLUMN id SET DEFAULT nextval('public.property_views_id_seq'::regclass);


--
-- Name: property_visits id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_visits ALTER COLUMN id SET DEFAULT nextval('public.property_visits_id_seq'::regclass);


--
-- Name: recent_viewed_properties id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.recent_viewed_properties ALTER COLUMN id SET DEFAULT nextval('public.recent_viewed_properties_id_seq'::regclass);


--
-- Name: reel_interactions id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.reel_interactions ALTER COLUMN id SET DEFAULT nextval('public.reel_interactions_id_seq'::regclass);


--
-- Name: review_likes id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.review_likes ALTER COLUMN id SET DEFAULT nextval('public.review_likes_id_seq'::regclass);


--
-- Name: role_requests id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.role_requests ALTER COLUMN id SET DEFAULT nextval('public.role_requests_id_seq'::regclass);


--
-- Name: sub_admin_permission id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sub_admin_permission ALTER COLUMN id SET DEFAULT nextval('public.sub_admin_permission_id_seq'::regclass);


--
-- Name: subscription_plan_features id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.subscription_plan_features ALTER COLUMN id SET DEFAULT nextval('public.subscription_plan_features_id_seq'::regclass);


--
-- Name: subscription_plans id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.subscription_plans ALTER COLUMN id SET DEFAULT nextval('public.subscription_plans_id_seq'::regclass);


--
-- Name: subscriptions id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.subscriptions ALTER COLUMN id SET DEFAULT nextval('public.subscriptions_id_seq'::regclass);


--
-- Name: user_following id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.user_following ALTER COLUMN id SET DEFAULT nextval('public.user_following_id_seq'::regclass);


--
-- Name: user_sessions id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.user_sessions ALTER COLUMN id SET DEFAULT nextval('public.user_sessions_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: advertisement_clicks advertisement_clicks_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.advertisement_clicks
    ADD CONSTRAINT advertisement_clicks_pkey PRIMARY KEY (id);


--
-- Name: advertisement_districts advertisement_districts_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.advertisement_districts
    ADD CONSTRAINT advertisement_districts_pkey PRIMARY KEY (advertisement_id, district_id);


--
-- Name: advertisements advertisements_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.advertisements
    ADD CONSTRAINT advertisements_pkey PRIMARY KEY (id);


--
-- Name: chat_attachments chat_attachments_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_attachments
    ADD CONSTRAINT chat_attachments_pkey PRIMARY KEY (id);


--
-- Name: chat_messages chat_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_messages
    ADD CONSTRAINT chat_messages_pkey PRIMARY KEY (id);


--
-- Name: chat_room_participants chat_room_participants_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_room_participants
    ADD CONSTRAINT chat_room_participants_pkey PRIMARY KEY (chat_room_id, user_id);


--
-- Name: chat_rooms chat_rooms_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_rooms
    ADD CONSTRAINT chat_rooms_pkey PRIMARY KEY (id);


--
-- Name: coupons coupons_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.coupons
    ADD CONSTRAINT coupons_pkey PRIMARY KEY (id);


--
-- Name: district_revenues district_revenues_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.district_revenues
    ADD CONSTRAINT district_revenues_pkey PRIMARY KEY (id);


--
-- Name: districts districts_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.districts
    ADD CONSTRAINT districts_pkey PRIMARY KEY (id);


--
-- Name: franchise_requests franchise_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.franchise_requests
    ADD CONSTRAINT franchise_requests_pkey PRIMARY KEY (id);


--
-- Name: franchisee_bank_details franchisee_bank_details_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.franchisee_bank_details
    ADD CONSTRAINT franchisee_bank_details_pkey PRIMARY KEY (id);


--
-- Name: franchisee_districts franchisee_districts_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.franchisee_districts
    ADD CONSTRAINT franchisee_districts_pkey PRIMARY KEY (id);


--
-- Name: franchisee_withdrawal_requests franchisee_withdrawal_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.franchisee_withdrawal_requests
    ADD CONSTRAINT franchisee_withdrawal_requests_pkey PRIMARY KEY (id);


--
-- Name: message_reports message_reports_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.message_reports
    ADD CONSTRAINT message_reports_pkey PRIMARY KEY (id);


--
-- Name: monthly_revenue_reports monthly_revenue_reports_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.monthly_revenue_reports
    ADD CONSTRAINT monthly_revenue_reports_pkey PRIMARY KEY (id);


--
-- Name: otps otps_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.otps
    ADD CONSTRAINT otps_pkey PRIMARY KEY (id);


--
-- Name: payment_transactions payment_transactions_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.payment_transactions
    ADD CONSTRAINT payment_transactions_pkey PRIMARY KEY (id);


--
-- Name: properties properties_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT properties_pkey PRIMARY KEY (id);


--
-- Name: property_additional_details property_additional_details_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_additional_details
    ADD CONSTRAINT property_additional_details_pkey PRIMARY KEY (property_id, title);


--
-- Name: property_images property_images_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_images
    ADD CONSTRAINT property_images_pkey PRIMARY KEY (property_id, image_order);


--
-- Name: property_inquiries property_inquiries_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_inquiries
    ADD CONSTRAINT property_inquiries_pkey PRIMARY KEY (id);


--
-- Name: property_inquiry_status_history property_inquiry_status_history_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_inquiry_status_history
    ADD CONSTRAINT property_inquiry_status_history_pkey PRIMARY KEY (id);


--
-- Name: property_reels property_reels_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_reels
    ADD CONSTRAINT property_reels_pkey PRIMARY KEY (id);


--
-- Name: property_reviews property_reviews_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_reviews
    ADD CONSTRAINT property_reviews_pkey PRIMARY KEY (id);


--
-- Name: property_update_fields property_update_fields_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_update_fields
    ADD CONSTRAINT property_update_fields_pkey PRIMARY KEY (update_request_id, field_name);


--
-- Name: property_update_new_fields property_update_new_fields_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_update_new_fields
    ADD CONSTRAINT property_update_new_fields_pkey PRIMARY KEY (update_request_id, field_name);


--
-- Name: property_update_requests property_update_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_update_requests
    ADD CONSTRAINT property_update_requests_pkey PRIMARY KEY (id);


--
-- Name: property_views property_views_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.property_views
    ADD CONSTRAINT property_views_pkey PRIMARY KEY (id);


--
-- Name: property_visits property_visits_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_visits
    ADD CONSTRAINT property_visits_pkey PRIMARY KEY (id);


--
-- Name: recent_viewed_properties recent_viewed_properties_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.recent_viewed_properties
    ADD CONSTRAINT recent_viewed_properties_pkey PRIMARY KEY (id);


--
-- Name: reel_interactions reel_interactions_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.reel_interactions
    ADD CONSTRAINT reel_interactions_pkey PRIMARY KEY (id);


--
-- Name: review_likes review_likes_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.review_likes
    ADD CONSTRAINT review_likes_pkey PRIMARY KEY (id);


--
-- Name: role_requests role_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.role_requests
    ADD CONSTRAINT role_requests_pkey PRIMARY KEY (id);


--
-- Name: sub_admin_permission sub_admin_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sub_admin_permission
    ADD CONSTRAINT sub_admin_permission_pkey PRIMARY KEY (id);


--
-- Name: subscription_plan_features subscription_plan_features_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.subscription_plan_features
    ADD CONSTRAINT subscription_plan_features_pkey PRIMARY KEY (id);


--
-- Name: subscription_plans subscription_plans_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.subscription_plans
    ADD CONSTRAINT subscription_plans_pkey PRIMARY KEY (id);


--
-- Name: subscriptions subscriptions_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.subscriptions
    ADD CONSTRAINT subscriptions_pkey PRIMARY KEY (id);


--
-- Name: chat_rooms uk342rvm3b6g6wdwmgkchus0jt9; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_rooms
    ADD CONSTRAINT uk342rvm3b6g6wdwmgkchus0jt9 UNIQUE (property_id, buyer_id, seller_id);


--
-- Name: subscription_plan_features uk_3xa1ea6h8b291tmeq2cvu1ki8; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.subscription_plan_features
    ADD CONSTRAINT uk_3xa1ea6h8b291tmeq2cvu1ki8 UNIQUE (plan_name);


--
-- Name: users uk_6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: users uk_8g3vuskpr8xdts014kd89p9vw; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_8g3vuskpr8xdts014kd89p9vw UNIQUE (aadhaar_number);


--
-- Name: property_reels uk_bcr5svphekujq943vf37a69y7; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_reels
    ADD CONSTRAINT uk_bcr5svphekujq943vf37a69y7 UNIQUE (public_id);


--
-- Name: coupons uk_cmua0fhw2jnb7553t22sr86um; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.coupons
    ADD CONSTRAINT uk_cmua0fhw2jnb7553t22sr86um UNIQUE (permanent_id);


--
-- Name: coupons uk_eplt0kkm9yf2of2lnx6c1oy9b; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.coupons
    ADD CONSTRAINT uk_eplt0kkm9yf2of2lnx6c1oy9b UNIQUE (code);


--
-- Name: users uk_fbd0iv7li32la6vnjmofeyl33; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_fbd0iv7li32la6vnjmofeyl33 UNIQUE (permanent_id);


--
-- Name: franchise_requests uk_franchise_request_user_district; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.franchise_requests
    ADD CONSTRAINT uk_franchise_request_user_district UNIQUE (user_id, district_id);


--
-- Name: franchisee_districts uk_franchisee_district; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.franchisee_districts
    ADD CONSTRAINT uk_franchisee_district UNIQUE (user_id, district_id);


--
-- Name: subscription_plans uk_oim1kg8luw8o6q3ayhcup6vtl; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.subscription_plans
    ADD CONSTRAINT uk_oim1kg8luw8o6q3ayhcup6vtl UNIQUE (name);


--
-- Name: payment_transactions uk_q4956xqxp3fsluc7cgxsynvg1; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.payment_transactions
    ADD CONSTRAINT uk_q4956xqxp3fsluc7cgxsynvg1 UNIQUE (reference_id);


--
-- Name: properties uk_qt5ntitbmbj5n2rw7w8m88t58; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT uk_qt5ntitbmbj5n2rw7w8m88t58 UNIQUE (permanent_id);


--
-- Name: property_update_requests uk_r1injknfeistyufinetvlqum6; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_update_requests
    ADD CONSTRAINT uk_r1injknfeistyufinetvlqum6 UNIQUE (request_id);


--
-- Name: users uk_r7c96a004bv8w16jgdm8imich; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_r7c96a004bv8w16jgdm8imich UNIQUE (mobile_number);


--
-- Name: user_following uk_user_following; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.user_following
    ADD CONSTRAINT uk_user_following UNIQUE (follower_id, followed_id);


--
-- Name: review_likes ukb74o5l2fmrgqg556d9nyop0ns; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.review_likes
    ADD CONSTRAINT ukb74o5l2fmrgqg556d9nyop0ns UNIQUE (review_id, user_id);


--
-- Name: user_favorites user_favorites_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.user_favorites
    ADD CONSTRAINT user_favorites_pkey PRIMARY KEY (user_id, property_id);


--
-- Name: user_following user_following_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.user_following
    ADD CONSTRAINT user_following_pkey PRIMARY KEY (id);


--
-- Name: user_preferences user_preferences_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.user_preferences
    ADD CONSTRAINT user_preferences_pkey PRIMARY KEY (user_id);


--
-- Name: user_sessions user_sessions_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.user_sessions
    ADD CONSTRAINT user_sessions_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: property_update_requests fk11larttpb9fio2ephh29r59dj; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_update_requests
    ADD CONSTRAINT fk11larttpb9fio2ephh29r59dj FOREIGN KEY (reviewed_by_franchisee) REFERENCES public.users(id);


--
-- Name: property_inquiry_status_history fk13sqm4jws7ffs6dwso8jsftui; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_inquiry_status_history
    ADD CONSTRAINT fk13sqm4jws7ffs6dwso8jsftui FOREIGN KEY (inquiry_id) REFERENCES public.property_inquiries(id);


--
-- Name: user_favorites fk1cgse7n60dc9ave03hmc48yvw; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.user_favorites
    ADD CONSTRAINT fk1cgse7n60dc9ave03hmc48yvw FOREIGN KEY (property_id) REFERENCES public.properties(id);


--
-- Name: monthly_revenue_reports fk1hqtu0f5mnxj59d84gt5sqske; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.monthly_revenue_reports
    ADD CONSTRAINT fk1hqtu0f5mnxj59d84gt5sqske FOREIGN KEY (bank_detail_id) REFERENCES public.franchisee_bank_details(id);


--
-- Name: property_luxurious_features fk1xej73jwfd2ac7j3ewew7tkur; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_luxurious_features
    ADD CONSTRAINT fk1xej73jwfd2ac7j3ewew7tkur FOREIGN KEY (property_id) REFERENCES public.properties(id);


--
-- Name: monthly_revenue_reports fk2a0qhxomt81x7slp0pkwgif8l; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.monthly_revenue_reports
    ADD CONSTRAINT fk2a0qhxomt81x7slp0pkwgif8l FOREIGN KEY (franchisee_id) REFERENCES public.users(id);


--
-- Name: properties fk32k2h9s30s0ukftb8hj947ef2; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT fk32k2h9s30s0ukftb8hj947ef2 FOREIGN KEY (owner_id) REFERENCES public.users(id);


--
-- Name: monthly_revenue_reports fk3enyhvsm9qh09bl7bnvie6v9a; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.monthly_revenue_reports
    ADD CONSTRAINT fk3enyhvsm9qh09bl7bnvie6v9a FOREIGN KEY (franchisee_district_id) REFERENCES public.franchisee_districts(id);


--
-- Name: message_reports fk3ljtxtkn76lgv4emjoqyu1jcl; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.message_reports
    ADD CONSTRAINT fk3ljtxtkn76lgv4emjoqyu1jcl FOREIGN KEY (processed_by_id) REFERENCES public.users(id);


--
-- Name: franchisee_districts fk46m3m7w8fhdy8phpf6l8gkk98; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.franchisee_districts
    ADD CONSTRAINT fk46m3m7w8fhdy8phpf6l8gkk98 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: properties fk4c905fx4hltxw2ig8ue4seb2b; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT fk4c905fx4hltxw2ig8ue4seb2b FOREIGN KEY (district_id) REFERENCES public.districts(id);


--
-- Name: user_favorites fk4sv7b9w9adr0fjnc4u10exlwm; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.user_favorites
    ADD CONSTRAINT fk4sv7b9w9adr0fjnc4u10exlwm FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: coupons fk5ta2iuowjf2sx01vtu35oi2an; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.coupons
    ADD CONSTRAINT fk5ta2iuowjf2sx01vtu35oi2an FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: advertisements fk63n1oftuihmri5ffn0t075at3; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.advertisements
    ADD CONSTRAINT fk63n1oftuihmri5ffn0t075at3 FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: chat_messages fk6wmkv55kk8d7rlwdef7op58se; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_messages
    ADD CONSTRAINT fk6wmkv55kk8d7rlwdef7op58se FOREIGN KEY (parent_message_id) REFERENCES public.chat_messages(id);


--
-- Name: property_views fk79c0jhqhf9eragpdy3xx7gerh; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.property_views
    ADD CONSTRAINT fk79c0jhqhf9eragpdy3xx7gerh FOREIGN KEY (property_id) REFERENCES public.properties(id);


--
-- Name: chat_rooms fk7ff9caadkg9nwim3x4a0fpdr7; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_rooms
    ADD CONSTRAINT fk7ff9caadkg9nwim3x4a0fpdr7 FOREIGN KEY (property_id) REFERENCES public.properties(id);


--
-- Name: subscriptions fk7lmb26g78gkedlvvtu96tmnkp; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.subscriptions
    ADD CONSTRAINT fk7lmb26g78gkedlvvtu96tmnkp FOREIGN KEY (coupon_id) REFERENCES public.coupons(id);


--
-- Name: franchisee_withdrawal_requests fk86244ss5hj06w8c6fom2dxh49; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.franchisee_withdrawal_requests
    ADD CONSTRAINT fk86244ss5hj06w8c6fom2dxh49 FOREIGN KEY (bank_detail_id) REFERENCES public.franchisee_bank_details(id);


--
-- Name: user_sessions fk8klxsgb8dcjjklmqebqp1twd5; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.user_sessions
    ADD CONSTRAINT fk8klxsgb8dcjjklmqebqp1twd5 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: property_reels fk985fqbx778wymxnj8t54o90u2; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_reels
    ADD CONSTRAINT fk985fqbx778wymxnj8t54o90u2 FOREIGN KEY (property_id) REFERENCES public.properties(id);


--
-- Name: property_update_requests fk9ec5cvt2777l0jajm36ygsh9e; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_update_requests
    ADD CONSTRAINT fk9ec5cvt2777l0jajm36ygsh9e FOREIGN KEY (property_id) REFERENCES public.properties(id);


--
-- Name: sub_admin_permission fk9jbvdms82wj3ki4a1h3k5312d; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sub_admin_permission
    ADD CONSTRAINT fk9jbvdms82wj3ki4a1h3k5312d FOREIGN KEY (subadmin_id) REFERENCES public.users(id);


--
-- Name: franchisee_withdrawal_requests fka5shosxxhbah975a9vh9ttyw2; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.franchisee_withdrawal_requests
    ADD CONSTRAINT fka5shosxxhbah975a9vh9ttyw2 FOREIGN KEY (franchisee_district_id) REFERENCES public.franchisee_districts(id);


--
-- Name: chat_messages fkbcsxusjp1v4rd8879fhvq8ssb; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_messages
    ADD CONSTRAINT fkbcsxusjp1v4rd8879fhvq8ssb FOREIGN KEY (chat_room_id) REFERENCES public.chat_rooms(id);


--
-- Name: user_following fkbhwj1yevud330mbllu4dt7gn7; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.user_following
    ADD CONSTRAINT fkbhwj1yevud330mbllu4dt7gn7 FOREIGN KEY (follower_id) REFERENCES public.users(id);


--
-- Name: property_reviews fkbt9so3io93w6x84d2fwyrjmo6; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_reviews
    ADD CONSTRAINT fkbt9so3io93w6x84d2fwyrjmo6 FOREIGN KEY (property_id) REFERENCES public.properties(id);


--
-- Name: chat_rooms fkc3j4hkkph4fy04l2t23kcl8os; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_rooms
    ADD CONSTRAINT fkc3j4hkkph4fy04l2t23kcl8os FOREIGN KEY (seller_id) REFERENCES public.users(id);


--
-- Name: recent_viewed_properties fkcd70dodts5h4gwex065ulr1l; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.recent_viewed_properties
    ADD CONSTRAINT fkcd70dodts5h4gwex065ulr1l FOREIGN KEY (property_id) REFERENCES public.properties(id);


--
-- Name: chat_attachments fkcgdxlis2e6x7fmupra1m2ro8g; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_attachments
    ADD CONSTRAINT fkcgdxlis2e6x7fmupra1m2ro8g FOREIGN KEY (chat_room_id) REFERENCES public.chat_rooms(id);


--
-- Name: properties fkcrwyjk6e8f0xkigem4cuiwuwk; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT fkcrwyjk6e8f0xkigem4cuiwuwk FOREIGN KEY (added_by_user_id) REFERENCES public.users(id);


--
-- Name: advertisement_districts fkcs2ahuhvruonpwti7vlhbpdiv; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.advertisement_districts
    ADD CONSTRAINT fkcs2ahuhvruonpwti7vlhbpdiv FOREIGN KEY (district_id) REFERENCES public.districts(id);


--
-- Name: review_likes fkd8ypeukfur64nnpylhapb9nrq; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.review_likes
    ADD CONSTRAINT fkd8ypeukfur64nnpylhapb9nrq FOREIGN KEY (review_id) REFERENCES public.property_reviews(id);


--
-- Name: advertisement_clicks fkdomyd7y5ty5s9n0qvdcoh9iq7; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.advertisement_clicks
    ADD CONSTRAINT fkdomyd7y5ty5s9n0qvdcoh9iq7 FOREIGN KEY (advertisement_id) REFERENCES public.advertisements(id);


--
-- Name: message_reports fkdr39pnhfxdskyr6w2c483443i; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.message_reports
    ADD CONSTRAINT fkdr39pnhfxdskyr6w2c483443i FOREIGN KEY (reporter_id) REFERENCES public.users(id);


--
-- Name: property_visits fkdv71b1uf04p81qll2imdexf0j; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_visits
    ADD CONSTRAINT fkdv71b1uf04p81qll2imdexf0j FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: property_images fkemw5i1cysiorfaxfba7tgtpiu; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_images
    ADD CONSTRAINT fkemw5i1cysiorfaxfba7tgtpiu FOREIGN KEY (property_id) REFERENCES public.properties(id);


--
-- Name: user_preferences fkepakpib0qnm82vmaiismkqf88; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.user_preferences
    ADD CONSTRAINT fkepakpib0qnm82vmaiismkqf88 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: property_update_requests fkeslrmtwiur5dujij8p3jph71q; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_update_requests
    ADD CONSTRAINT fkeslrmtwiur5dujij8p3jph71q FOREIGN KEY (requested_by) REFERENCES public.users(id);


--
-- Name: property_security_features fkf4446eb1vasd9iywo8hyae7qn; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_security_features
    ADD CONSTRAINT fkf4446eb1vasd9iywo8hyae7qn FOREIGN KEY (property_id) REFERENCES public.properties(id);


--
-- Name: property_amenities fkflie0u6fwgptlapkkqgpyh05; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_amenities
    ADD CONSTRAINT fkflie0u6fwgptlapkkqgpyh05 FOREIGN KEY (property_id) REFERENCES public.properties(id);


--
-- Name: advertisements fkfqc6bdl285dur5vlppe7npxqk; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.advertisements
    ADD CONSTRAINT fkfqc6bdl285dur5vlppe7npxqk FOREIGN KEY (district_id) REFERENCES public.districts(id);


--
-- Name: subscriptions fkg41f5iev0mretaqhvepf0lks0; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.subscriptions
    ADD CONSTRAINT fkg41f5iev0mretaqhvepf0lks0 FOREIGN KEY (plan_id) REFERENCES public.subscription_plans(id);


--
-- Name: chat_messages fkgiqeap8ays4lf684x7m0r2729; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_messages
    ADD CONSTRAINT fkgiqeap8ays4lf684x7m0r2729 FOREIGN KEY (sender_id) REFERENCES public.users(id);


--
-- Name: reel_interactions fkgn7h3f66mb7x3qshwcsjduqr8; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.reel_interactions
    ADD CONSTRAINT fkgn7h3f66mb7x3qshwcsjduqr8 FOREIGN KEY (reel_id) REFERENCES public.property_reels(id);


--
-- Name: chat_rooms fkhbap39tpdxxb39tf4qmovcb18; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_rooms
    ADD CONSTRAINT fkhbap39tpdxxb39tf4qmovcb18 FOREIGN KEY (buyer_id) REFERENCES public.users(id);


--
-- Name: user_roles fkhfh9dx7w3ubf1co1vdev94g3f; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: subscriptions fkhro52ohfqfbay9774bev0qinr; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.subscriptions
    ADD CONSTRAINT fkhro52ohfqfbay9774bev0qinr FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: chat_attachments fkiapn4tta73bdo003lo7j8smeb; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_attachments
    ADD CONSTRAINT fkiapn4tta73bdo003lo7j8smeb FOREIGN KEY (message_id) REFERENCES public.chat_messages(id);


--
-- Name: property_update_fields fkiee325f0t6itoaeifke6kp7ye; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_update_fields
    ADD CONSTRAINT fkiee325f0t6itoaeifke6kp7ye FOREIGN KEY (update_request_id) REFERENCES public.property_update_requests(id);


--
-- Name: property_reviews fkiqbf6l5j7ce3b3ow3nmowex8p; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_reviews
    ADD CONSTRAINT fkiqbf6l5j7ce3b3ow3nmowex8p FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: chat_room_participants fkiqfwuqd8c8i7hrkukc0rii5pg; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_room_participants
    ADD CONSTRAINT fkiqfwuqd8c8i7hrkukc0rii5pg FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: property_update_new_fields fkj8q52f78iqeelarqumol8uylk; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_update_new_fields
    ADD CONSTRAINT fkj8q52f78iqeelarqumol8uylk FOREIGN KEY (update_request_id) REFERENCES public.property_update_requests(id);


--
-- Name: franchisee_districts fkjepeowejpjs5sod3ic6ilhdsd; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.franchisee_districts
    ADD CONSTRAINT fkjepeowejpjs5sod3ic6ilhdsd FOREIGN KEY (franchise_request_id) REFERENCES public.franchise_requests(id);


--
-- Name: property_reels fkjm632b2nggtly9vkvqpkqs89o; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_reels
    ADD CONSTRAINT fkjm632b2nggtly9vkvqpkqs89o FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: chat_room_participants fkkrenof4in9x16he7adf6g058f; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_room_participants
    ADD CONSTRAINT fkkrenof4in9x16he7adf6g058f FOREIGN KEY (chat_room_id) REFERENCES public.chat_rooms(id);


--
-- Name: advertisement_districts fkky3cuf10e82tmu1ns8glxwi7k; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.advertisement_districts
    ADD CONSTRAINT fkky3cuf10e82tmu1ns8glxwi7k FOREIGN KEY (advertisement_id) REFERENCES public.advertisements(id);


--
-- Name: role_requests fkljihuio5sbmdk3jjmtm3lh72j; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.role_requests
    ADD CONSTRAINT fkljihuio5sbmdk3jjmtm3lh72j FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: property_features fkn3ifctki5nhmhys68v543j26q; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_features
    ADD CONSTRAINT fkn3ifctki5nhmhys68v543j26q FOREIGN KEY (property_id) REFERENCES public.properties(id);


--
-- Name: chat_attachments fkn97gcmis6g51p671fld3y6ion; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.chat_attachments
    ADD CONSTRAINT fkn97gcmis6g51p671fld3y6ion FOREIGN KEY (uploader_id) REFERENCES public.users(id);


--
-- Name: role_requests fknjnedtcahhbvqu8468xwnas0t; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.role_requests
    ADD CONSTRAINT fknjnedtcahhbvqu8468xwnas0t FOREIGN KEY (processed_by) REFERENCES public.users(id);


--
-- Name: review_likes fknual15vv88tiqnwmi60tb2l8d; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.review_likes
    ADD CONSTRAINT fknual15vv88tiqnwmi60tb2l8d FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: user_following fko2k0mm91betcv557qhb90x7k8; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.user_following
    ADD CONSTRAINT fko2k0mm91betcv557qhb90x7k8 FOREIGN KEY (followed_id) REFERENCES public.users(id);


--
-- Name: payment_transactions fko4904wf3pxl3mbx5ntf52h70w; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.payment_transactions
    ADD CONSTRAINT fko4904wf3pxl3mbx5ntf52h70w FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: property_views fkoxuwei1q0495e0hncb48n2rth; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.property_views
    ADD CONSTRAINT fkoxuwei1q0495e0hncb48n2rth FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: property_additional_details fkpd5sra0i817wmu49qdmjvugyw; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_additional_details
    ADD CONSTRAINT fkpd5sra0i817wmu49qdmjvugyw FOREIGN KEY (property_id) REFERENCES public.properties(id);


--
-- Name: franchise_requests fkpig53k2b6m0m11r1i625g83ov; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.franchise_requests
    ADD CONSTRAINT fkpig53k2b6m0m11r1i625g83ov FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: message_reports fkppu19lyler1ycvw6x7ycpm4n; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.message_reports
    ADD CONSTRAINT fkppu19lyler1ycvw6x7ycpm4n FOREIGN KEY (message_id) REFERENCES public.chat_messages(id);


--
-- Name: property_update_requests fkpvxux14mi1u2rjo0d92at1e86; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_update_requests
    ADD CONSTRAINT fkpvxux14mi1u2rjo0d92at1e86 FOREIGN KEY (reviewed_by_admin) REFERENCES public.users(id);


--
-- Name: franchisee_bank_details fkq989hr0r3ehxd8apiel16axst; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.franchisee_bank_details
    ADD CONSTRAINT fkq989hr0r3ehxd8apiel16axst FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: district_revenues fkrgdkwoy9ugofabjxf90cqdva6; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.district_revenues
    ADD CONSTRAINT fkrgdkwoy9ugofabjxf90cqdva6 FOREIGN KEY (franchisee_district_id) REFERENCES public.franchisee_districts(id);


--
-- Name: reel_interactions fkrroo3mqbcdfv4rdenqv61gsmg; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.reel_interactions
    ADD CONSTRAINT fkrroo3mqbcdfv4rdenqv61gsmg FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: recent_viewed_properties fks29n2igtmneeppndwkftuisfu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.recent_viewed_properties
    ADD CONSTRAINT fks29n2igtmneeppndwkftuisfu FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: role_request_documents fksc5tn0r90rov8pqr6um7fxf13; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.role_request_documents
    ADD CONSTRAINT fksc5tn0r90rov8pqr6um7fxf13 FOREIGN KEY (role_request_id) REFERENCES public.role_requests(id);


--
-- Name: property_update_requests fksh5sepdsyrk8pkhx6bpsnje1l; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_update_requests
    ADD CONSTRAINT fksh5sepdsyrk8pkhx6bpsnje1l FOREIGN KEY (franchisee_id) REFERENCES public.users(id);


--
-- Name: property_visits fkt9r4shq1r6iy8tkn8mefiwhwe; Type: FK CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_visits
    ADD CONSTRAINT fkt9r4shq1r6iy8tkn8mefiwhwe FOREIGN KEY (property_id) REFERENCES public.properties(id);


--
-- Name: TABLE property_views; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.property_views TO nearprop_user;


--
-- Name: SEQUENCE property_views_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.property_views_id_seq TO nearprop_user;


--
-- Name: TABLE recent_viewed_properties; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.recent_viewed_properties TO nearprop_user;


--
-- Name: SEQUENCE recent_viewed_properties_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.recent_viewed_properties_id_seq TO nearprop_user;


--
-- Name: TABLE sub_admin_permission; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.sub_admin_permission TO nearprop_user;


--
-- Name: SEQUENCE sub_admin_permission_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.sub_admin_permission_id_seq TO nearprop_user;


--
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON SEQUENCES TO nearprop_user;


--
-- Name: DEFAULT PRIVILEGES FOR FUNCTIONS; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON FUNCTIONS TO nearprop_user;


--
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON TABLES TO nearprop_user;


--
-- PostgreSQL database dump complete
--

\unrestrict t7FlJXtBeVLH3Hq9HnbIcN5OMHHAiTG0Om6kBmwRzMI3b9pKlF3Lr59WPKeahjb


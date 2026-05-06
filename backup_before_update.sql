--
-- PostgreSQL database dump
--

\restrict PmAV6LidW13zvMPK0YqulEa4JA3Aafz4y0CBfYx9xZTRfpvXsmqbQu67sxsTIFp

-- Dumped from database version 15.14 (Debian 15.14-1.pgdg13+1)
-- Dumped by pg_dump version 15.14 (Debian 15.14-1.pgdg13+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

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
    CONSTRAINT advertisement_clicks_click_type_check CHECK (((click_type)::text = ANY (ARRAY[('VIEW'::character varying)::text, ('WEBSITE'::character varying)::text, ('WHATSAPP'::character varying)::text, ('PHONE'::character varying)::text, ('INSTAGRAM'::character varying)::text, ('FACEBOOK'::character varying)::text, ('YOUTUBE'::character varying)::text, ('TWITTER'::character varying)::text, ('LINKEDIN'::character varying)::text, ('OTHER'::character varying)::text])))
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


ALTER TABLE public.advertisement_clicks_id_seq OWNER TO nearprop_user;

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


ALTER TABLE public.advertisements_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT chat_attachments_type_check CHECK (((type)::text = ANY (ARRAY[('IMAGE'::character varying)::text, ('DOCUMENT'::character varying)::text, ('VIDEO'::character varying)::text, ('AUDIO'::character varying)::text])))
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


ALTER TABLE public.chat_attachments_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT chat_messages_status_check CHECK (((status)::text = ANY (ARRAY[('SENT'::character varying)::text, ('DELIVERED'::character varying)::text, ('READ'::character varying)::text])))
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


ALTER TABLE public.chat_messages_id_seq OWNER TO nearprop_user;

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


ALTER TABLE public.chat_rooms_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT coupons_discount_type_check CHECK (((discount_type)::text = ANY (ARRAY[('PERCENTAGE'::character varying)::text, ('FIXED_AMOUNT'::character varying)::text]))),
    CONSTRAINT coupons_subscription_type_check CHECK (((subscription_type)::text = ANY (ARRAY[('SELLER'::character varying)::text, ('ADVISOR'::character varying)::text, ('DEVELOPER'::character varying)::text, ('FRANCHISEE'::character varying)::text, ('PROPERTY'::character varying)::text])))
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


ALTER TABLE public.coupons_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT district_revenues_payment_status_check CHECK (((payment_status)::text = ANY (ARRAY[('PENDING'::character varying)::text, ('PAID'::character varying)::text, ('CANCELLED'::character varying)::text]))),
    CONSTRAINT district_revenues_revenue_type_check CHECK (((revenue_type)::text = ANY (ARRAY[('PROPERTY_LISTING'::character varying)::text, ('SUBSCRIPTION_PAYMENT'::character varying)::text, ('VISIT_BOOKING'::character varying)::text, ('TRANSACTION_FEE'::character varying)::text, ('MARKETING_FEE'::character varying)::text, ('WITHDRAWAL'::character varying)::text, ('OTHER'::character varying)::text])))
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


ALTER TABLE public.district_revenues_id_seq OWNER TO nearprop_user;

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


ALTER TABLE public.districts_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT franchise_requests_status_check CHECK (((status)::text = ANY (ARRAY[('PENDING'::character varying)::text, ('APPROVED'::character varying)::text, ('REJECTED'::character varying)::text])))
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


ALTER TABLE public.franchise_requests_id_seq OWNER TO nearprop_user;

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


ALTER TABLE public.franchisee_bank_details_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT franchisee_districts_status_check CHECK (((status)::text = ANY (ARRAY[('ACTIVE'::character varying)::text, ('SUSPENDED'::character varying)::text, ('TERMINATED'::character varying)::text, ('PENDING_APPROVAL'::character varying)::text])))
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


ALTER TABLE public.franchisee_districts_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT franchisee_withdrawal_requests_status_check CHECK (((status)::text = ANY (ARRAY[('PENDING'::character varying)::text, ('APPROVED'::character varying)::text, ('PAID'::character varying)::text, ('REJECTED'::character varying)::text])))
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


ALTER TABLE public.franchisee_withdrawal_requests_id_seq OWNER TO nearprop_user;

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


ALTER TABLE public.message_reports_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT monthly_revenue_reports_report_status_check CHECK (((report_status)::text = ANY (ARRAY[('PENDING'::character varying)::text, ('PAID'::character varying)::text, ('CANCELLED'::character varying)::text])))
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


ALTER TABLE public.monthly_revenue_reports_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT otps_type_check CHECK (((type)::text = ANY (ARRAY[('MOBILE'::character varying)::text, ('EMAIL'::character varying)::text])))
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


ALTER TABLE public.otps_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT payment_transactions_payment_method_check CHECK (((payment_method)::text = ANY (ARRAY[('CREDIT_CARD'::character varying)::text, ('DEBIT_CARD'::character varying)::text, ('NET_BANKING'::character varying)::text, ('UPI'::character varying)::text, ('WALLET'::character varying)::text, ('BANK_TRANSFER'::character varying)::text, ('EMI'::character varying)::text, ('OTHER'::character varying)::text]))),
    CONSTRAINT payment_transactions_payment_type_check CHECK (((payment_type)::text = ANY (ARRAY[('SUBSCRIPTION'::character varying)::text, ('PROPERTY_LISTING'::character varying)::text, ('FRANCHISE_FEE'::character varying)::text, ('SERVICE_FEE'::character varying)::text, ('REEL_PURCHASE'::character varying)::text, ('OTHER'::character varying)::text]))),
    CONSTRAINT payment_transactions_status_check CHECK (((status)::text = ANY (ARRAY[('INITIATED'::character varying)::text, ('PROCESSING'::character varying)::text, ('COMPLETED'::character varying)::text, ('FAILED'::character varying)::text, ('CANCELLED'::character varying)::text, ('REFUNDED'::character varying)::text, ('PARTIALLY_REFUNDED'::character varying)::text])))
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


ALTER TABLE public.payment_transactions_id_seq OWNER TO nearprop_user;

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
    area double precision NOT NULL,
    availability character varying(255),
    bathrooms integer NOT NULL,
    bedrooms integer NOT NULL,
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
    CONSTRAINT properties_label_check CHECK (((label)::text = ANY (ARRAY[('GOLDEN_OFFER'::character varying)::text, ('HOT_OFFER'::character varying)::text, ('OPEN_HOUSE'::character varying)::text, ('SOLD'::character varying)::text, ('DEVELOPER'::character varying)::text]))),
    CONSTRAINT properties_status_check CHECK (((status)::text = ANY (ARRAY[('AVAILABLE'::character varying)::text, ('SOLD'::character varying)::text, ('RENTED'::character varying)::text, ('UNDER_CONTRACT'::character varying)::text, ('PENDING_APPROVAL'::character varying)::text, ('INACTIVE'::character varying)::text, ('FOR_RENT'::character varying)::text, ('FOR_SALE'::character varying)::text, ('BLOCKED'::character varying)::text, ('ACTIVE'::character varying)::text, ('PENDING'::character varying)::text, ('DELETED'::character varying)::text]))),
    CONSTRAINT properties_type_check CHECK (((type)::text = ANY (ARRAY[('APARTMENT'::character varying)::text, ('VILLA'::character varying)::text, ('HOUSE'::character varying)::text, ('PLOT'::character varying)::text, ('COMMERCIAL'::character varying)::text, ('OFFICE_SPACE'::character varying)::text, ('SHOP'::character varying)::text, ('WAREHOUSE'::character varying)::text, ('FARMLAND'::character varying)::text, ('PG_HOSTEL'::character varying)::text, ('CONDO'::character varying)::text, ('MULTI_FAMILY_HOME'::character varying)::text, ('SINGLE_FAMILY_HOME'::character varying)::text, ('STUDIO'::character varying)::text, ('LAND'::character varying)::text])))
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


ALTER TABLE public.properties_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT property_inquiries_info_type_check CHECK (((info_type)::text = ANY (ARRAY[('RENT'::character varying)::text, ('SELL'::character varying)::text, ('PURCHASE'::character varying)::text, ('OTHER'::character varying)::text]))),
    CONSTRAINT property_inquiries_property_type_check CHECK (((property_type)::text = ANY (ARRAY[('APARTMENT'::character varying)::text, ('VILLA'::character varying)::text, ('HOUSE'::character varying)::text, ('PLOT'::character varying)::text, ('COMMERCIAL'::character varying)::text, ('OFFICE_SPACE'::character varying)::text, ('SHOP'::character varying)::text, ('WAREHOUSE'::character varying)::text, ('FARMLAND'::character varying)::text, ('PG_HOSTEL'::character varying)::text, ('CONDO'::character varying)::text, ('MULTI_FAMILY_HOME'::character varying)::text, ('SINGLE_FAMILY_HOME'::character varying)::text, ('STUDIO'::character varying)::text, ('LAND'::character varying)::text]))),
    CONSTRAINT property_inquiries_status_check CHECK (((status)::text = ANY (ARRAY[('IN_REVIEW'::character varying)::text, ('COMPLETED'::character varying)::text, ('OTHER'::character varying)::text])))
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


ALTER TABLE public.property_inquiries_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT property_inquiry_status_history_status_check CHECK (((status)::text = ANY (ARRAY[('IN_REVIEW'::character varying)::text, ('COMPLETED'::character varying)::text, ('OTHER'::character varying)::text])))
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


ALTER TABLE public.property_inquiry_status_history_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT property_reels_processing_status_check CHECK (((processing_status)::text = ANY (ARRAY[('QUEUED'::character varying)::text, ('PROCESSING'::character varying)::text, ('COMPLETED'::character varying)::text, ('FAILED'::character varying)::text]))),
    CONSTRAINT property_reels_status_check CHECK (((status)::text = ANY (ARRAY[('DRAFT'::character varying)::text, ('PUBLISHED'::character varying)::text, ('ARCHIVED'::character varying)::text, ('REJECTED'::character varying)::text, ('HIDDEN'::character varying)::text])))
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


ALTER TABLE public.property_reels_id_seq OWNER TO nearprop_user;

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


ALTER TABLE public.property_reviews_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT property_update_requests_status_check CHECK (((status)::text = ANY (ARRAY[('PENDING'::character varying)::text, ('APPROVED'::character varying)::text, ('REJECTED'::character varying)::text, ('CANCELLED'::character varying)::text])))
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


ALTER TABLE public.property_update_requests_id_seq OWNER TO nearprop_user;

--
-- Name: property_update_requests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.property_update_requests_id_seq OWNED BY public.property_update_requests.id;


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
    CONSTRAINT property_visits_status_check CHECK (((status)::text = ANY (ARRAY[('PENDING'::character varying)::text, ('CONFIRMED'::character varying)::text, ('COMPLETED'::character varying)::text, ('CANCELLED'::character varying)::text, ('RESCHEDULED'::character varying)::text])))
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


ALTER TABLE public.property_visits_id_seq OWNER TO nearprop_user;

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
-- Name: reel_interactions; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.reel_interactions (
    id bigint NOT NULL,
    comment character varying(1000),
    created_at timestamp(6) without time zone NOT NULL,
    type character varying(255) NOT NULL,
    reel_id bigint NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT reel_interactions_type_check CHECK (((type)::text = ANY (ARRAY[('LIKE'::character varying)::text, ('COMMENT'::character varying)::text, ('FOLLOW'::character varying)::text, ('VIEW'::character varying)::text, ('SHARE'::character varying)::text, ('SAVE'::character varying)::text])))
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


ALTER TABLE public.reel_interactions_id_seq OWNER TO nearprop_user;

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


ALTER TABLE public.review_likes_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT role_requests_requested_role_check CHECK (((requested_role)::text = ANY (ARRAY[('USER'::character varying)::text, ('SELLER'::character varying)::text, ('ADVISOR'::character varying)::text, ('DEVELOPER'::character varying)::text, ('FRANCHISEE'::character varying)::text, ('ADMIN'::character varying)::text]))),
    CONSTRAINT role_requests_status_check CHECK (((status)::text = ANY (ARRAY[('PENDING'::character varying)::text, ('APPROVED'::character varying)::text, ('REJECTED'::character varying)::text])))
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


ALTER TABLE public.role_requests_id_seq OWNER TO nearprop_user;

--
-- Name: role_requests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.role_requests_id_seq OWNED BY public.role_requests.id;


--
-- Name: sub_admin_permission; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.sub_admin_permission (
    id bigint NOT NULL,
    action character varying(255),
    module character varying(255),
    subadmin_id bigint NOT NULL,
    CONSTRAINT sub_admin_permission_action_check CHECK (((action)::text = ANY (ARRAY[('VIEW'::character varying)::text, ('CREATE'::character varying)::text, ('UPDATE'::character varying)::text, ('DELETE'::character varying)::text]))),
    CONSTRAINT sub_admin_permission_module_check CHECK (((module)::text = ANY (ARRAY[('PROPERTY'::character varying)::text, ('ADVERTISEMENT'::character varying)::text, ('FRANCHISEE'::character varying)::text, ('SUBSCRIPTION'::character varying)::text, ('WITHDRAW'::character varying)::text])))
);


ALTER TABLE public.sub_admin_permission OWNER TO nearprop_user;

--
-- Name: sub_admin_permission_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.sub_admin_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sub_admin_permission_id_seq OWNER TO nearprop_user;

--
-- Name: sub_admin_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.sub_admin_permission_id_seq OWNED BY public.sub_admin_permission.id;


--
-- Name: sub_admins; Type: TABLE; Schema: public; Owner: nearprop_user
--

CREATE TABLE public.sub_admins (
    id integer NOT NULL,
    name character varying(255),
    email character varying(255),
    mobile_number character varying(20)
);


ALTER TABLE public.sub_admins OWNER TO nearprop_user;

--
-- Name: sub_admins_id_seq; Type: SEQUENCE; Schema: public; Owner: nearprop_user
--

CREATE SEQUENCE public.sub_admins_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sub_admins_id_seq OWNER TO nearprop_user;

--
-- Name: sub_admins_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nearprop_user
--

ALTER SEQUENCE public.sub_admins_id_seq OWNED BY public.sub_admins.id;


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
    CONSTRAINT subscription_plan_features_plan_type_check CHECK (((plan_type)::text = ANY (ARRAY[('USER'::character varying)::text, ('SELLER'::character varying)::text, ('ADVISOR'::character varying)::text, ('DEVELOPER'::character varying)::text, ('FRANCHISEE'::character varying)::text])))
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


ALTER TABLE public.subscription_plan_features_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT subscription_plans_type_check CHECK (((type)::text = ANY (ARRAY[('SELLER'::character varying)::text, ('ADVISOR'::character varying)::text, ('DEVELOPER'::character varying)::text, ('FRANCHISEE'::character varying)::text, ('PROPERTY'::character varying)::text])))
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


ALTER TABLE public.subscription_plans_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT subscriptions_status_check CHECK (((status)::text = ANY (ARRAY[('PENDING_PAYMENT'::character varying)::text, ('ACTIVE'::character varying)::text, ('EXPIRED'::character varying)::text, ('CANCELLED'::character varying)::text, ('CONTENT_HIDDEN'::character varying)::text, ('CONTENT_DELETED'::character varying)::text])))
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


ALTER TABLE public.subscriptions_id_seq OWNER TO nearprop_user;

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


ALTER TABLE public.user_following_id_seq OWNER TO nearprop_user;

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
    CONSTRAINT user_roles_role_check CHECK (((role)::text = ANY ((ARRAY['ADMIN'::character varying, 'USER'::character varying, 'SUBADMIN'::character varying])::text[])))
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


ALTER TABLE public.user_sessions_id_seq OWNER TO nearprop_user;

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


ALTER TABLE public.users_id_seq OWNER TO nearprop_user;

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
-- Name: property_visits id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_visits ALTER COLUMN id SET DEFAULT nextval('public.property_visits_id_seq'::regclass);


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
-- Name: sub_admin_permission id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.sub_admin_permission ALTER COLUMN id SET DEFAULT nextval('public.sub_admin_permission_id_seq'::regclass);


--
-- Name: sub_admins id; Type: DEFAULT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.sub_admins ALTER COLUMN id SET DEFAULT nextval('public.sub_admins_id_seq'::regclass);


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
-- Data for Name: advertisement_clicks; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.advertisement_clicks (id, click_type, created_at, ip_address, referrer, user_agent, user_district, user_id, advertisement_id) FROM stdin;
\.


--
-- Data for Name: advertisement_districts; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.advertisement_districts (advertisement_id, district_id) FROM stdin;
2	360
4	332
5	332
\.


--
-- Data for Name: advertisements; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.advertisements (id, active, additional_info, banner_image_url, click_count, created_at, day_before_notification_sent, description, district_name, email_address, expiry_notification_sent, facebook_url, hours_before_notification_sent, instagram_url, latitude, linkedin_url, longitude, phone_clicks, phone_number, radius_km, social_media_clicks, target_location, title, twitter_url, updated_at, valid_from, valid_until, video_url, view_count, website_clicks, website_url, whatsapp_clicks, whatsapp_number, youtube_url, created_by, district_id) FROM stdin;
2	t	\N	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/advertisements/media/advertisements/admin/24_rohit-patel/title/images/title-39bc9a25-2e1e-4547-b723-c4e92656ac0e.png	0	2025-11-19 11:10:31.337304	f	description	Ujjain	rohitkiaaan@gmail.com	f	https://www.facebook.com/luxuryhomes.blr	f	https://www.instagram.com/saim_7024?igsh=MXF6M2w5aXJ5Y3F4Zw==	22.7196	ttps://www.linkdin.com/luxuryhomes.blr	75.8577	0	6265861847	50	0	Ujjain	title 	\N	2025-11-19 11:10:31.33731	2025-11-20 11:09:00	2025-12-06 11:09:00	\N	0	0	http://nearprop.com/	0	6265861847	https://youtu.be/upU0OcE658E?si=yZs8jnCXx5Qm8jpD	24	360
4	t	\N	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/advertisements/media/advertisements/admin/54_admin/hfdh/images/hfdh-2e470ea3-f1b2-4c8b-8185-8692cfb52c36.jpg	0	2025-11-19 12:38:59.970303	f	hfjfj	Indore	\N	f	\N	f	\N	22.7196	\N	75.8577	0	\N	50	0	Indore	hfdh	\N	2025-11-19 12:38:59.970311	2025-11-19 12:38:00	2025-11-28 12:38:00	\N	0	0	\N	0	\N	\N	54	332
5	t	\N	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/advertisements/media/advertisements/admin/54_admin/hfdh/images/hfdh-7e8fc32f-ffbf-4645-9a8c-6a9bf2611c35.jpg	0	2025-11-19 12:39:00.723948	f	hfjfj	Indore	\N	f	\N	f	\N	22.7196	\N	75.8577	0	\N	50	0	Indore	hfdh	\N	2025-11-19 12:39:00.723955	2025-11-19 12:38:00	2025-11-28 12:38:00	\N	0	0	\N	0	\N	\N	54	332
\.


--
-- Data for Name: chat_attachments; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.chat_attachments (id, content_type, created_at, file_name, file_path, file_size, file_url, height, thumbnail_path, thumbnail_url, type, width, chat_room_id, message_id, uploader_id) FROM stdin;
\.


--
-- Data for Name: chat_messages; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.chat_messages (id, content, created_at, delivered_at, is_edited, edited_at, is_admin_message, read_at, is_reported, status, updated_at, is_warned, chat_room_id, parent_message_id, sender_id) FROM stdin;
2	i want to buy this	2025-11-14 13:10:10.502421	\N	f	\N	f	2025-11-14 13:38:57.413074	f	READ	2025-11-14 13:10:10.502423	f	3	\N	22
1	hy	2025-11-14 13:00:39.266919	\N	f	\N	f	2025-11-14 13:39:02.282247	f	READ	2025-11-14 13:00:39.266923	f	1	\N	23
4	hello 	2025-11-14 13:43:20.130954	\N	f	\N	f	\N	f	SENT	2025-11-14 13:43:20.130956	f	1	\N	26
5	asdfghjk	2025-11-14 13:44:13.314685	\N	f	\N	f	\N	f	SENT	2025-11-14 13:44:13.314687	f	1	\N	26
6	ad	2025-11-14 13:52:46.362942	\N	f	\N	f	\N	f	SENT	2025-11-14 13:52:46.362945	f	1	\N	26
7	hiiiiiii	2025-11-14 14:01:15.284387	\N	f	\N	f	2025-11-15 04:48:52.032444	f	READ	2025-11-14 14:01:15.28439	f	2	\N	26
8	asfdfgghfjg	2025-11-14 14:01:54.061693	\N	f	\N	f	2025-11-15 04:48:52.03311	f	READ	2025-11-14 14:01:54.061695	f	2	\N	26
3	Hello mam	2025-11-14 13:42:53.09243	\N	f	\N	f	2025-11-15 04:50:22.041356	f	READ	2025-11-14 13:42:53.092433	f	3	\N	26
9	hello Abhishek	2025-11-15 05:37:41.272856	\N	f	\N	f	\N	f	SENT	2025-11-15 05:37:41.27286	f	5	\N	26
10	hy	2025-11-15 05:38:01.386826	\N	f	\N	f	\N	f	SENT	2025-11-15 05:38:01.386829	f	2	\N	22
11	bbdhwbdwd	2025-11-15 05:38:45.390848	\N	f	\N	f	\N	f	SENT	2025-11-15 05:38:45.39085	f	2	\N	22
12	hello	2025-11-15 05:39:03.684647	\N	f	\N	f	2025-11-15 05:39:11.052318	f	READ	2025-11-15 05:39:03.68465	f	2	\N	26
14	ok	2025-11-15 08:48:50.979784	\N	f	\N	f	\N	f	SENT	2025-11-15 08:48:50.979789	f	1	\N	26
16	Hello Chirag sir , I want to buy this property	2025-11-15 09:17:21.286982	\N	f	\N	f	\N	f	SENT	2025-11-15 09:17:21.286986	f	5	\N	26
13	hello	2025-11-15 06:46:52.187201	\N	f	\N	f	2025-11-15 13:53:51.44006	f	READ	2025-11-15 06:46:52.187205	f	4	\N	26
15	hello bue	2025-11-15 09:16:29.941247	\N	f	\N	f	2025-11-17 11:21:27.898119	f	READ	2025-11-15 09:16:29.941251	f	2	\N	26
17	hi	2025-12-04 08:09:44.022469	\N	f	\N	f	\N	f	SENT	2025-12-04 08:09:44.022483	f	6	\N	71
\.


--
-- Data for Name: chat_room_participants; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.chat_room_participants (chat_room_id, user_id) FROM stdin;
1	23
1	26
2	22
2	26
3	26
3	22
4	2
4	26
5	26
6	28
6	71
\.


--
-- Data for Name: chat_rooms; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.chat_rooms (id, created_at, last_message_at, status, title, updated_at, buyer_id, property_id, seller_id) FROM stdin;
3	2025-11-14 13:09:58.697534	2025-11-14 13:42:53.093337	ACTIVE	Chat for Title	2025-11-14 13:42:53.093589	22	3	26
4	2025-11-14 13:46:35.47175	2025-11-15 06:46:52.188115	ACTIVE	Interested in property	2025-11-15 06:46:52.188376	26	2	2
1	2025-11-14 13:00:33.39469	2025-11-15 08:48:50.980785	ACTIVE	Chat for Title	2025-11-15 08:48:50.981125	23	3	26
2	2025-11-14 13:03:49.499178	2025-11-15 09:16:29.942087	ACTIVE	Interested in property	2025-11-15 09:16:29.942285	26	1	22
5	2025-11-14 14:21:48.903177	2025-11-15 09:17:21.287636	ACTIVE	Interested in property	2025-11-15 09:17:21.2879	26	3	26
6	2025-12-04 08:09:38.087203	2025-12-04 08:09:44.024084	ACTIVE	Interested in property	2025-12-04 08:09:44.025242	71	11	28
\.


--
-- Data for Name: coupons; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.coupons (id, is_active, code, created_at, current_uses, description, discount_amount, discount_percentage, discount_type, max_discount, max_uses, permanent_id, subscription_type, updated_at, valid_from, valid_until, created_by) FROM stdin;
\.


--
-- Data for Name: district_revenues; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.district_revenues (id, amount, company_revenue, created_at, description, district_id, district_name, franchisee_commission, payment_date, payment_reference, payment_status, property_id, revenue_type, state, subscription_id, transaction_date, transaction_id, updated_at, franchisee_district_id) FROM stdin;
1	0.00	0.00	2025-11-11 08:37:12.139769	Subscription payment for user 22 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	7	2025-11-11 08:37:12.139473	\N	2025-11-11 08:37:12.139772	1
2	0.00	0.00	2025-11-11 08:37:27.613204	Subscription payment for user 2 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	8	2025-11-11 08:37:27.613106	\N	2025-11-11 08:37:27.613207	1
3	0.00	0.00	2025-11-11 09:07:26.94181	Subscription payment for user 25 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	9	2025-11-11 09:07:26.94172	\N	2025-11-11 09:07:26.941812	1
4	0.00	0.00	2025-11-11 10:28:46.055043	Subscription payment for user 13 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	10	2025-11-11 10:28:46.054902	\N	2025-11-11 10:28:46.055046	1
5	0.00	0.00	2025-11-11 10:40:35.395751	Subscription payment for user 26 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	11	2025-11-11 10:40:35.395622	\N	2025-11-11 10:40:35.395753	1
6	0.00	0.00	2025-11-14 11:52:19.457481	Subscription payment for user 26 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PENDING	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	12	2025-11-14 11:52:19.457316	\N	2025-11-14 11:52:19.457483	1
7	0.00	0.00	2025-11-14 11:54:21.312947	Subscription payment for user 26 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PENDING	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	13	2025-11-14 11:54:21.312867	\N	2025-11-14 11:54:21.312949	1
8	0.00	0.00	2025-11-15 08:29:46.219278	Subscription payment for user 23 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	14	2025-11-15 08:29:46.219196	\N	2025-11-15 08:29:46.21928	1
9	0.00	0.00	2025-11-15 08:49:47.385226	Subscription payment for user 28 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	15	2025-11-15 08:49:47.385139	\N	2025-11-15 08:49:47.385228	1
10	0.00	0.00	2025-11-15 09:47:50.736294	Subscription payment for user 29 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	16	2025-11-15 09:47:50.736238	\N	2025-11-15 09:47:50.736296	1
11	0.00	0.00	2025-11-15 13:18:33.884062	Subscription payment for user 30 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	17	2025-11-15 13:18:33.883979	\N	2025-11-15 13:18:33.884064	1
12	0.00	0.00	2025-11-15 13:33:42.054246	Subscription payment for user 31 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	18	2025-11-15 13:33:42.054159	\N	2025-11-15 13:33:42.054247	1
13	0.00	0.00	2025-11-16 04:18:34.106698	Subscription payment for user 32 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	19	2025-11-16 04:18:34.106597	\N	2025-11-16 04:18:34.106699	1
14	0.00	0.00	2025-11-16 04:29:25.253246	Subscription payment for user 33 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	20	2025-11-16 04:29:25.253159	\N	2025-11-16 04:29:25.253247	1
15	0.00	0.00	2025-11-16 04:35:13.156065	Subscription payment for user 34 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	21	2025-11-16 04:35:13.156008	\N	2025-11-16 04:35:13.156067	1
16	0.00	0.00	2025-11-16 08:59:14.98984	Subscription payment for user 35 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	22	2025-11-16 08:59:14.989777	\N	2025-11-16 08:59:14.989841	1
17	0.00	0.00	2025-11-17 07:07:09.806266	Subscription payment for user 36 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	23	2025-11-17 07:07:09.80576	\N	2025-11-17 07:07:09.806279	1
18	0.00	0.00	2025-11-17 08:32:45.072496	Subscription payment for user 15 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	24	2025-11-17 08:32:45.07231	\N	2025-11-17 08:32:45.072506	1
19	0.00	0.00	2025-11-17 10:44:20.431122	Subscription payment for user 26 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PENDING	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	25	2025-11-17 10:44:20.431012	\N	2025-11-17 10:44:20.431126	1
20	0.00	0.00	2025-11-17 10:45:35.285257	Subscription payment for user 26 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PENDING	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	26	2025-11-17 10:45:35.285168	\N	2025-11-17 10:45:35.285261	1
21	0.00	0.00	2025-11-17 10:45:36.253653	Subscription payment for user 26 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PENDING	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	27	2025-11-17 10:45:36.253489	\N	2025-11-17 10:45:36.253658	1
22	0.00	0.00	2025-11-17 12:42:34.911755	Subscription payment for user 41 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	28	2025-11-17 12:42:34.911201	\N	2025-11-17 12:42:34.911774	1
23	0.00	0.00	2025-12-02 17:36:09.556827	Subscription payment for user 64 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	31	2025-12-02 17:36:09.555649	\N	2025-12-02 17:36:09.55683	1
24	0.00	0.00	2025-12-04 05:43:26.991222	Subscription payment for user 68 (Plan: Basic Property Listing)	332	Indore	0.00	\N	\N	PAID	\N	SUBSCRIPTION_PAYMENT	Madhya Pradesh	32	2025-12-04 05:43:26.989532	\N	2025-12-04 05:43:26.991233	1
\.


--
-- Data for Name: districts; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.districts (id, active, city, created_at, latitude, longitude, name, pincode, radius_km, revenue_share_percentage, state, updated_at) FROM stdin;
1	t	Nicobars	2025-11-05 10:08:29.280513	\N	\N	Nicobars	744301	\N	50.00	Andaman and Nicobar Islands	2025-11-05 10:08:29.280615
2	t	North and Middle Andaman	2025-11-05 10:08:29.31532	\N	\N	North and Middle Andaman	744201	\N	50.00	Andaman and Nicobar Islands	2025-11-05 10:08:29.315356
3	t	South Andaman	2025-11-05 10:08:29.31804	\N	\N	South Andaman	744101	\N	50.00	Andaman and Nicobar Islands	2025-11-05 10:08:29.318066
4	t	Anantapur	2025-11-05 10:08:29.319706	\N	\N	Anantapur	515001	\N	50.00	Andhra Pradesh	2025-11-05 10:08:29.31972
5	t	Chittoor	2025-11-05 10:08:29.321161	\N	\N	Chittoor	517001	\N	50.00	Andhra Pradesh	2025-11-05 10:08:29.321174
6	t	East Godavari	2025-11-05 10:08:29.323085	\N	\N	East Godavari	533001	\N	50.00	Andhra Pradesh	2025-11-05 10:08:29.323101
7	t	Guntur	2025-11-05 10:08:29.324709	\N	\N	Guntur	522001	\N	50.00	Andhra Pradesh	2025-11-05 10:08:29.324723
8	t	Krishna	2025-11-05 10:08:29.326097	\N	\N	Krishna	520001	\N	50.00	Andhra Pradesh	2025-11-05 10:08:29.32611
9	t	Kurnool	2025-11-05 10:08:29.327468	\N	\N	Kurnool	518001	\N	50.00	Andhra Pradesh	2025-11-05 10:08:29.327481
10	t	Nellore	2025-11-05 10:08:29.328954	\N	\N	Nellore	524001	\N	50.00	Andhra Pradesh	2025-11-05 10:08:29.328999
11	t	Prakasam	2025-11-05 10:08:29.33067	\N	\N	Prakasam	523001	\N	50.00	Andhra Pradesh	2025-11-05 10:08:29.330686
12	t	Srikakulam	2025-11-05 10:08:29.3323	\N	\N	Srikakulam	532001	\N	50.00	Andhra Pradesh	2025-11-05 10:08:29.332314
13	t	Visakhapatnam	2025-11-05 10:08:29.333742	\N	\N	Visakhapatnam	530001	\N	50.00	Andhra Pradesh	2025-11-05 10:08:29.333756
14	t	Vizianagaram	2025-11-05 10:08:29.335162	\N	\N	Vizianagaram	535001	\N	50.00	Andhra Pradesh	2025-11-05 10:08:29.335174
15	t	West Godavari	2025-11-05 10:08:29.337259	\N	\N	West Godavari	534001	\N	50.00	Andhra Pradesh	2025-11-05 10:08:29.337274
16	t	YSR Kadapa	2025-11-05 10:08:29.339065	\N	\N	YSR Kadapa	516001	\N	50.00	Andhra Pradesh	2025-11-05 10:08:29.339079
17	t	Tawang	2025-11-05 10:08:29.340581	\N	\N	Tawang	790104	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.340599
18	t	West Kameng	2025-11-05 10:08:29.342008	\N	\N	West Kameng	790114	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.342021
19	t	East Kameng	2025-11-05 10:08:29.343345	\N	\N	East Kameng	790102	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.343358
20	t	Papum Pare	2025-11-05 10:08:29.344814	\N	\N	Papum Pare	791111	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.344827
21	t	Kurung Kumey	2025-11-05 10:08:29.346461	\N	\N	Kurung Kumey	791118	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.346477
22	t	Kra Daadi	2025-11-05 10:08:29.347971	\N	\N	Kra Daadi	791121	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.347985
23	t	Lower Subansiri	2025-11-05 10:08:29.349523	\N	\N	Lower Subansiri	791120	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.349545
24	t	Upper Subansiri	2025-11-05 10:08:29.35104	\N	\N	Upper Subansiri	791122	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.35107
25	t	West Siang	2025-11-05 10:08:29.352502	\N	\N	West Siang	791125	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.352514
26	t	East Siang	2025-11-05 10:08:29.353937	\N	\N	East Siang	791102	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.353949
27	t	Siang	2025-11-05 10:08:29.355412	\N	\N	Siang	791002	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.355424
28	t	Upper Siang	2025-11-05 10:08:29.356832	\N	\N	Upper Siang	791002	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.356849
29	t	Lower Siang	2025-11-05 10:08:29.358536	\N	\N	Lower Siang	791125	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.358554
30	t	Lower Dibang Valley	2025-11-05 10:08:29.360263	\N	\N	Lower Dibang Valley	792110	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.36028
31	t	Dibang Valley	2025-11-05 10:08:29.362109	\N	\N	Dibang Valley	792101	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.362125
32	t	Anjaw	2025-11-05 10:08:29.363695	\N	\N	Anjaw	792104	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.363712
33	t	Lohit	2025-11-05 10:08:29.365457	\N	\N	Lohit	792001	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.365475
34	t	Namsai	2025-11-05 10:08:29.367216	\N	\N	Namsai	792103	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.367234
35	t	Changlang	2025-11-05 10:08:29.369084	\N	\N	Changlang	792120	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.369102
36	t	Tirap	2025-11-05 10:08:29.371138	\N	\N	Tirap	792129	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.371163
37	t	Longding	2025-11-05 10:08:29.373152	\N	\N	Longding	792131	\N	50.00	Arunachal Pradesh	2025-11-05 10:08:29.373171
38	t	Baksa	2025-11-05 10:08:29.37496	\N	\N	Baksa	781372	\N	50.00	Assam	2025-11-05 10:08:29.374979
39	t	Barpeta	2025-11-05 10:08:29.376658	\N	\N	Barpeta	781301	\N	50.00	Assam	2025-11-05 10:08:29.376694
40	t	Biswanath	2025-11-05 10:08:29.378375	\N	\N	Biswanath	784176	\N	50.00	Assam	2025-11-05 10:08:29.378391
41	t	Bongaigaon	2025-11-05 10:08:29.379981	\N	\N	Bongaigaon	783380	\N	50.00	Assam	2025-11-05 10:08:29.379998
42	t	Cachar	2025-11-05 10:08:29.381715	\N	\N	Cachar	788001	\N	50.00	Assam	2025-11-05 10:08:29.381734
43	t	Charaideo	2025-11-05 10:08:29.383906	\N	\N	Charaideo	785688	\N	50.00	Assam	2025-11-05 10:08:29.383925
44	t	Chirang	2025-11-05 10:08:29.385743	\N	\N	Chirang	783390	\N	50.00	Assam	2025-11-05 10:08:29.385761
45	t	Darrang	2025-11-05 10:08:29.38751	\N	\N	Darrang	784145	\N	50.00	Assam	2025-11-05 10:08:29.387542
46	t	Dhemaji	2025-11-05 10:08:29.389088	\N	\N	Dhemaji	787057	\N	50.00	Assam	2025-11-05 10:08:29.389106
47	t	Dhubri	2025-11-05 10:08:29.390681	\N	\N	Dhubri	783301	\N	50.00	Assam	2025-11-05 10:08:29.390698
48	t	Dibrugarh	2025-11-05 10:08:29.392326	\N	\N	Dibrugarh	786001	\N	50.00	Assam	2025-11-05 10:08:29.392341
49	t	Dima Hasao	2025-11-05 10:08:29.393866	\N	\N	Dima Hasao	788819	\N	50.00	Assam	2025-11-05 10:08:29.393882
50	t	Goalpara	2025-11-05 10:08:29.395471	\N	\N	Goalpara	783101	\N	50.00	Assam	2025-11-05 10:08:29.395486
51	t	Golaghat	2025-11-05 10:08:29.397141	\N	\N	Golaghat	785621	\N	50.00	Assam	2025-11-05 10:08:29.397158
52	t	Hailakandi	2025-11-05 10:08:29.39878	\N	\N	Hailakandi	788151	\N	50.00	Assam	2025-11-05 10:08:29.398796
53	t	Hojai	2025-11-05 10:08:29.40034	\N	\N	Hojai	782301	\N	50.00	Assam	2025-11-05 10:08:29.400354
54	t	Jorhat	2025-11-05 10:08:29.401994	\N	\N	Jorhat	785001	\N	50.00	Assam	2025-11-05 10:08:29.40201
55	t	Kamrup	2025-11-05 10:08:29.403609	\N	\N	Kamrup	781101	\N	50.00	Assam	2025-11-05 10:08:29.403624
56	t	Kamrup Metropolitan	2025-11-05 10:08:29.405154	\N	\N	Kamrup Metropolitan	781001	\N	50.00	Assam	2025-11-05 10:08:29.405169
57	t	Karbi Anglong	2025-11-05 10:08:29.406685	\N	\N	Karbi Anglong	782460	\N	50.00	Assam	2025-11-05 10:08:29.406701
58	t	Karimganj	2025-11-05 10:08:29.408181	\N	\N	Karimganj	788710	\N	50.00	Assam	2025-11-05 10:08:29.408196
59	t	Kokrajhar	2025-11-05 10:08:29.40977	\N	\N	Kokrajhar	783370	\N	50.00	Assam	2025-11-05 10:08:29.409785
60	t	Lakhimpur	2025-11-05 10:08:29.411285	\N	\N	Lakhimpur	787001	\N	50.00	Assam	2025-11-05 10:08:29.411301
61	t	Majuli	2025-11-05 10:08:29.412879	\N	\N	Majuli	785104	\N	50.00	Assam	2025-11-05 10:08:29.412904
62	t	Morigaon	2025-11-05 10:08:29.414388	\N	\N	Morigaon	782105	\N	50.00	Assam	2025-11-05 10:08:29.414401
63	t	Nagaon	2025-11-05 10:08:29.415904	\N	\N	Nagaon	782001	\N	50.00	Assam	2025-11-05 10:08:29.415921
64	t	Nalbari	2025-11-05 10:08:29.417693	\N	\N	Nalbari	781335	\N	50.00	Assam	2025-11-05 10:08:29.417711
65	t	Sivasagar	2025-11-05 10:08:29.419479	\N	\N	Sivasagar	785640	\N	50.00	Assam	2025-11-05 10:08:29.419494
66	t	Sonitpur	2025-11-05 10:08:29.421069	\N	\N	Sonitpur	784001	\N	50.00	Assam	2025-11-05 10:08:29.421083
67	t	South Salmara-Mankachar	2025-11-05 10:08:29.422341	\N	\N	South Salmara-Mankachar	783135	\N	50.00	Assam	2025-11-05 10:08:29.422352
68	t	Tinsukia	2025-11-05 10:08:29.423535	\N	\N	Tinsukia	786125	\N	50.00	Assam	2025-11-05 10:08:29.423547
69	t	Udalguri	2025-11-05 10:08:29.424604	\N	\N	Udalguri	784509	\N	50.00	Assam	2025-11-05 10:08:29.424618
70	t	West Karbi Anglong	2025-11-05 10:08:29.42574	\N	\N	West Karbi Anglong	782447	\N	50.00	Assam	2025-11-05 10:08:29.425751
71	t	Araria	2025-11-05 10:08:29.426903	\N	\N	Araria	854311	\N	50.00	Bihar	2025-11-05 10:08:29.426914
72	t	Arwal	2025-11-05 10:08:29.428013	\N	\N	Arwal	804401	\N	50.00	Bihar	2025-11-05 10:08:29.428023
73	t	Aurangabad	2025-11-05 10:08:29.429401	\N	\N	Aurangabad	824101	\N	50.00	Bihar	2025-11-05 10:08:29.429412
74	t	Banka	2025-11-05 10:08:29.430639	\N	\N	Banka	813102	\N	50.00	Bihar	2025-11-05 10:08:29.43065
75	t	Begusarai	2025-11-05 10:08:29.431698	\N	\N	Begusarai	851101	\N	50.00	Bihar	2025-11-05 10:08:29.431709
76	t	Bhagalpur	2025-11-05 10:08:29.43287	\N	\N	Bhagalpur	812001	\N	50.00	Bihar	2025-11-05 10:08:29.432881
77	t	Bhojpur	2025-11-05 10:08:29.434264	\N	\N	Bhojpur	802301	\N	50.00	Bihar	2025-11-05 10:08:29.434276
78	t	Buxar	2025-11-05 10:08:29.435402	\N	\N	Buxar	802101	\N	50.00	Bihar	2025-11-05 10:08:29.435413
79	t	Darbhanga	2025-11-05 10:08:29.436568	\N	\N	Darbhanga	846004	\N	50.00	Bihar	2025-11-05 10:08:29.436578
80	t	East Champaran	2025-11-05 10:08:29.437943	\N	\N	East Champaran	845401	\N	50.00	Bihar	2025-11-05 10:08:29.437958
81	t	Gaya	2025-11-05 10:08:29.43916	\N	\N	Gaya	823001	\N	50.00	Bihar	2025-11-05 10:08:29.439172
82	t	Gopalganj	2025-11-05 10:08:29.440478	\N	\N	Gopalganj	841428	\N	50.00	Bihar	2025-11-05 10:08:29.440492
83	t	Jamui	2025-11-05 10:08:29.442018	\N	\N	Jamui	811307	\N	50.00	Bihar	2025-11-05 10:08:29.442031
84	t	Jehanabad	2025-11-05 10:08:29.443529	\N	\N	Jehanabad	804408	\N	50.00	Bihar	2025-11-05 10:08:29.443545
85	t	Kaimur	2025-11-05 10:08:29.44516	\N	\N	Kaimur	821101	\N	50.00	Bihar	2025-11-05 10:08:29.445175
86	t	Katihar	2025-11-05 10:08:29.446734	\N	\N	Katihar	854105	\N	50.00	Bihar	2025-11-05 10:08:29.446751
87	t	Khagaria	2025-11-05 10:08:29.448739	\N	\N	Khagaria	851204	\N	50.00	Bihar	2025-11-05 10:08:29.448754
88	t	Kishanganj	2025-11-05 10:08:29.450168	\N	\N	Kishanganj	855107	\N	50.00	Bihar	2025-11-05 10:08:29.450183
89	t	Lakhisarai	2025-11-05 10:08:29.451517	\N	\N	Lakhisarai	811311	\N	50.00	Bihar	2025-11-05 10:08:29.45153
90	t	Madhepura	2025-11-05 10:08:29.452821	\N	\N	Madhepura	852113	\N	50.00	Bihar	2025-11-05 10:08:29.452833
91	t	Madhubani	2025-11-05 10:08:29.454096	\N	\N	Madhubani	847211	\N	50.00	Bihar	2025-11-05 10:08:29.454111
92	t	Munger	2025-11-05 10:08:29.455403	\N	\N	Munger	811201	\N	50.00	Bihar	2025-11-05 10:08:29.455418
93	t	Muzaffarpur	2025-11-05 10:08:29.456868	\N	\N	Muzaffarpur	842001	\N	50.00	Bihar	2025-11-05 10:08:29.456902
94	t	Nalanda	2025-11-05 10:08:29.458336	\N	\N	Nalanda	803101	\N	50.00	Bihar	2025-11-05 10:08:29.458351
95	t	Nawada	2025-11-05 10:08:29.459798	\N	\N	Nawada	805110	\N	50.00	Bihar	2025-11-05 10:08:29.459812
96	t	Patna	2025-11-05 10:08:29.461245	\N	\N	Patna	800001	\N	50.00	Bihar	2025-11-05 10:08:29.461259
97	t	Purnia	2025-11-05 10:08:29.462675	\N	\N	Purnia	854301	\N	50.00	Bihar	2025-11-05 10:08:29.46269
98	t	Rohtas	2025-11-05 10:08:29.464477	\N	\N	Rohtas	821311	\N	50.00	Bihar	2025-11-05 10:08:29.464492
99	t	Saharsa	2025-11-05 10:08:29.465881	\N	\N	Saharsa	852201	\N	50.00	Bihar	2025-11-05 10:08:29.465917
100	t	Samastipur	2025-11-05 10:08:29.467293	\N	\N	Samastipur	848101	\N	50.00	Bihar	2025-11-05 10:08:29.467309
101	t	Saran	2025-11-05 10:08:29.46862	\N	\N	Saran	841301	\N	50.00	Bihar	2025-11-05 10:08:29.468632
102	t	Sheikhpura	2025-11-05 10:08:29.46999	\N	\N	Sheikhpura	811105	\N	50.00	Bihar	2025-11-05 10:08:29.470004
103	t	Sheohar	2025-11-05 10:08:29.471333	\N	\N	Sheohar	843329	\N	50.00	Bihar	2025-11-05 10:08:29.471345
104	t	Sitamarhi	2025-11-05 10:08:29.472647	\N	\N	Sitamarhi	843301	\N	50.00	Bihar	2025-11-05 10:08:29.472689
105	t	Siwan	2025-11-05 10:08:29.47408	\N	\N	Siwan	841226	\N	50.00	Bihar	2025-11-05 10:08:29.474093
106	t	Supaul	2025-11-05 10:08:29.475461	\N	\N	Supaul	852131	\N	50.00	Bihar	2025-11-05 10:08:29.475475
107	t	Vaishali	2025-11-05 10:08:29.476795	\N	\N	Vaishali	844101	\N	50.00	Bihar	2025-11-05 10:08:29.476807
108	t	West Champaran	2025-11-05 10:08:29.478025	\N	\N	West Champaran	845438	\N	50.00	Bihar	2025-11-05 10:08:29.478036
109	t	Chandigarh	2025-11-05 10:08:29.479166	\N	\N	Chandigarh	160017	\N	50.00	Chandigarh	2025-11-05 10:08:29.479194
110	t	Balod	2025-11-05 10:08:29.48041	\N	\N	Balod	491226	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.480421
111	t	Baloda Bazar	2025-11-05 10:08:29.481839	\N	\N	Baloda Bazar	493332	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.481939
112	t	Balrampur	2025-11-05 10:08:29.483303	\N	\N	Balrampur	497119	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.483318
113	t	Bastar	2025-11-05 10:08:29.484827	\N	\N	Bastar	494001	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.484841
114	t	Bemetara	2025-11-05 10:08:29.486168	\N	\N	Bemetara	491335	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.486179
115	t	Bijapur	2025-11-05 10:08:29.487337	\N	\N	Bijapur	494444	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.487347
116	t	Bilaspur	2025-11-05 10:08:29.488556	\N	\N	Bilaspur	495001	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.488567
117	t	Dantewada	2025-11-05 10:08:29.489897	\N	\N	Dantewada	494449	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.489909
118	t	Dhamtari	2025-11-05 10:08:29.491179	\N	\N	Dhamtari	493773	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.491191
119	t	Durg	2025-11-05 10:08:29.492566	\N	\N	Durg	491001	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.492578
120	t	Gariaband	2025-11-05 10:08:29.494061	\N	\N	Gariaband	493889	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.494074
121	t	Janjgir-Champa	2025-11-05 10:08:29.495303	\N	\N	Janjgir-Champa	495668	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.495326
122	t	Jashpur	2025-11-05 10:08:29.496974	\N	\N	Jashpur	496331	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.496984
123	t	Kabirdham	2025-11-05 10:08:29.49847	\N	\N	Kabirdham	491995	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.498498
124	t	Kanker	2025-11-05 10:08:29.499753	\N	\N	Kanker	494334	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.499763
125	t	Kondagaon	2025-11-05 10:08:29.501045	\N	\N	Kondagaon	494226	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.501055
126	t	Korba	2025-11-05 10:08:29.502423	\N	\N	Korba	495677	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.502433
127	t	Koriya	2025-11-05 10:08:29.503709	\N	\N	Koriya	497335	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.503718
128	t	Mahasamund	2025-11-05 10:08:29.505437	\N	\N	Mahasamund	493445	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.505449
129	t	Mungeli	2025-11-05 10:08:29.50713	\N	\N	Mungeli	495334	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.507142
130	t	Narayanpur	2025-11-05 10:08:29.508234	\N	\N	Narayanpur	494661	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.508244
131	t	Raigarh	2025-11-05 10:08:29.509292	\N	\N	Raigarh	496001	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.509302
132	t	Raipur	2025-11-05 10:08:29.510345	\N	\N	Raipur	492001	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.510354
133	t	Rajnandgaon	2025-11-05 10:08:29.511451	\N	\N	Rajnandgaon	491441	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.511461
134	t	Sukma	2025-11-05 10:08:29.512533	\N	\N	Sukma	494111	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.512542
135	t	Surajpur	2025-11-05 10:08:29.513618	\N	\N	Surajpur	497229	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.513628
136	t	Surguja	2025-11-05 10:08:29.514733	\N	\N	Surguja	497101	\N	50.00	Chhattisgarh	2025-11-05 10:08:29.514742
137	t	Daman	2025-11-05 10:08:29.515845	\N	\N	Daman	396210	\N	50.00	Dadra and Nagar Haveli and Daman and Diu	2025-11-05 10:08:29.515856
138	t	Diu	2025-11-05 10:08:29.516999	\N	\N	Diu	362520	\N	50.00	Dadra and Nagar Haveli and Daman and Diu	2025-11-05 10:08:29.51701
139	t	Dadra and Nagar Haveli	2025-11-05 10:08:29.518261	\N	\N	Dadra and Nagar Haveli	396230	\N	50.00	Dadra and Nagar Haveli and Daman and Diu	2025-11-05 10:08:29.51827
140	t	Central Delhi	2025-11-05 10:08:29.519447	\N	\N	Central Delhi	110001	\N	50.00	Delhi	2025-11-05 10:08:29.519457
141	t	East Delhi	2025-11-05 10:08:29.520548	\N	\N	East Delhi	110031	\N	50.00	Delhi	2025-11-05 10:08:29.520557
142	t	New Delhi	2025-11-05 10:08:29.52172	\N	\N	New Delhi	110011	\N	50.00	Delhi	2025-11-05 10:08:29.521729
143	t	North Delhi	2025-11-05 10:08:29.522852	\N	\N	North Delhi	110007	\N	50.00	Delhi	2025-11-05 10:08:29.522861
144	t	North East Delhi	2025-11-05 10:08:29.524102	\N	\N	North East Delhi	110053	\N	50.00	Delhi	2025-11-05 10:08:29.524111
145	t	North West Delhi	2025-11-05 10:08:29.525224	\N	\N	North West Delhi	110033	\N	50.00	Delhi	2025-11-05 10:08:29.525234
146	t	Shahdara	2025-11-05 10:08:29.52643	\N	\N	Shahdara	110032	\N	50.00	Delhi	2025-11-05 10:08:29.526439
147	t	South Delhi	2025-11-05 10:08:29.527691	\N	\N	South Delhi	110017	\N	50.00	Delhi	2025-11-05 10:08:29.527705
148	t	South East Delhi	2025-11-05 10:08:29.528961	\N	\N	South East Delhi	110025	\N	50.00	Delhi	2025-11-05 10:08:29.52897
149	t	South West Delhi	2025-11-05 10:08:29.53029	\N	\N	South West Delhi	110010	\N	50.00	Delhi	2025-11-05 10:08:29.5303
150	t	West Delhi	2025-11-05 10:08:29.531458	\N	\N	West Delhi	110026	\N	50.00	Delhi	2025-11-05 10:08:29.531468
151	t	North Goa	2025-11-05 10:08:29.532702	\N	\N	North Goa	403001	\N	50.00	Goa	2025-11-05 10:08:29.532711
152	t	South Goa	2025-11-05 10:08:29.533962	\N	\N	South Goa	403601	\N	50.00	Goa	2025-11-05 10:08:29.533972
153	t	Ahmedabad	2025-11-05 10:08:29.535252	\N	\N	Ahmedabad	380001	\N	50.00	Gujarat	2025-11-05 10:08:29.535262
154	t	Amreli	2025-11-05 10:08:29.536408	\N	\N	Amreli	365601	\N	50.00	Gujarat	2025-11-05 10:08:29.536435
155	t	Anand	2025-11-05 10:08:29.537722	\N	\N	Anand	388001	\N	50.00	Gujarat	2025-11-05 10:08:29.537738
156	t	Aravalli	2025-11-05 10:08:29.538781	\N	\N	Aravalli	383001	\N	50.00	Gujarat	2025-11-05 10:08:29.538792
157	t	Banaskantha	2025-11-05 10:08:29.539987	\N	\N	Banaskantha	385001	\N	50.00	Gujarat	2025-11-05 10:08:29.539997
158	t	Bharuch	2025-11-05 10:08:29.541243	\N	\N	Bharuch	392001	\N	50.00	Gujarat	2025-11-05 10:08:29.541252
159	t	Bhavnagar	2025-11-05 10:08:29.542481	\N	\N	Bhavnagar	364001	\N	50.00	Gujarat	2025-11-05 10:08:29.542491
160	t	Botad	2025-11-05 10:08:29.543707	\N	\N	Botad	364710	\N	50.00	Gujarat	2025-11-05 10:08:29.543717
161	t	Chhota Udaipur	2025-11-05 10:08:29.545276	\N	\N	Chhota Udaipur	391165	\N	50.00	Gujarat	2025-11-05 10:08:29.545286
162	t	Dahod	2025-11-05 10:08:29.546524	\N	\N	Dahod	389151	\N	50.00	Gujarat	2025-11-05 10:08:29.546536
163	t	Dang	2025-11-05 10:08:29.547827	\N	\N	Dang	394710	\N	50.00	Gujarat	2025-11-05 10:08:29.547838
164	t	Devbhumi Dwarka	2025-11-05 10:08:29.549046	\N	\N	Devbhumi Dwarka	361305	\N	50.00	Gujarat	2025-11-05 10:08:29.549061
165	t	Gandhinagar	2025-11-05 10:08:29.55027	\N	\N	Gandhinagar	382010	\N	50.00	Gujarat	2025-11-05 10:08:29.55028
166	t	Gir Somnath	2025-11-05 10:08:29.551479	\N	\N	Gir Somnath	362265	\N	50.00	Gujarat	2025-11-05 10:08:29.55149
167	t	Jamnagar	2025-11-05 10:08:29.552699	\N	\N	Jamnagar	361001	\N	50.00	Gujarat	2025-11-05 10:08:29.552709
168	t	Junagadh	2025-11-05 10:08:29.554084	\N	\N	Junagadh	362001	\N	50.00	Gujarat	2025-11-05 10:08:29.554095
169	t	Kheda	2025-11-05 10:08:29.555389	\N	\N	Kheda	387001	\N	50.00	Gujarat	2025-11-05 10:08:29.555399
170	t	Kutch	2025-11-05 10:08:29.556527	\N	\N	Kutch	370001	\N	50.00	Gujarat	2025-11-05 10:08:29.556537
171	t	Mahisagar	2025-11-05 10:08:29.557975	\N	\N	Mahisagar	389230	\N	50.00	Gujarat	2025-11-05 10:08:29.557988
172	t	Mehsana	2025-11-05 10:08:29.559377	\N	\N	Mehsana	384001	\N	50.00	Gujarat	2025-11-05 10:08:29.559387
173	t	Morbi	2025-11-05 10:08:29.560507	\N	\N	Morbi	363641	\N	50.00	Gujarat	2025-11-05 10:08:29.560517
174	t	Narmada	2025-11-05 10:08:29.561654	\N	\N	Narmada	393145	\N	50.00	Gujarat	2025-11-05 10:08:29.561673
175	t	Navsari	2025-11-05 10:08:29.562742	\N	\N	Navsari	396445	\N	50.00	Gujarat	2025-11-05 10:08:29.562751
176	t	Panchmahal	2025-11-05 10:08:29.563806	\N	\N	Panchmahal	389001	\N	50.00	Gujarat	2025-11-05 10:08:29.563834
177	t	Patan	2025-11-05 10:08:29.565103	\N	\N	Patan	384265	\N	50.00	Gujarat	2025-11-05 10:08:29.565146
178	t	Porbandar	2025-11-05 10:08:29.566289	\N	\N	Porbandar	360575	\N	50.00	Gujarat	2025-11-05 10:08:29.566314
179	t	Rajkot	2025-11-05 10:08:29.567603	\N	\N	Rajkot	360001	\N	50.00	Gujarat	2025-11-05 10:08:29.56761
180	t	Sabarkantha	2025-11-05 10:08:29.56875	\N	\N	Sabarkantha	383001	\N	50.00	Gujarat	2025-11-05 10:08:29.568757
181	t	Surat	2025-11-05 10:08:29.569806	\N	\N	Surat	395003	\N	50.00	Gujarat	2025-11-05 10:08:29.569813
182	t	Surendranagar	2025-11-05 10:08:29.570836	\N	\N	Surendranagar	363001	\N	50.00	Gujarat	2025-11-05 10:08:29.570844
183	t	Tapi	2025-11-05 10:08:29.571807	\N	\N	Tapi	394650	\N	50.00	Gujarat	2025-11-05 10:08:29.571814
184	t	Vadodara	2025-11-05 10:08:29.572954	\N	\N	Vadodara	390001	\N	50.00	Gujarat	2025-11-05 10:08:29.572961
185	t	Valsad	2025-11-05 10:08:29.573963	\N	\N	Valsad	396001	\N	50.00	Gujarat	2025-11-05 10:08:29.57397
186	t	Ambala	2025-11-05 10:08:29.575064	\N	\N	Ambala	133001	\N	50.00	Haryana	2025-11-05 10:08:29.575071
187	t	Bhiwani	2025-11-05 10:08:29.576357	\N	\N	Bhiwani	127021	\N	50.00	Haryana	2025-11-05 10:08:29.576364
188	t	Charkhi Dadri	2025-11-05 10:08:29.577661	\N	\N	Charkhi Dadri	127306	\N	50.00	Haryana	2025-11-05 10:08:29.577672
189	t	Faridabad	2025-11-05 10:08:29.578901	\N	\N	Faridabad	121001	\N	50.00	Haryana	2025-11-05 10:08:29.578908
190	t	Fatehabad	2025-11-05 10:08:29.58007	\N	\N	Fatehabad	125050	\N	50.00	Haryana	2025-11-05 10:08:29.580077
191	t	Gurugram	2025-11-05 10:08:29.581398	\N	\N	Gurugram	122001	\N	50.00	Haryana	2025-11-05 10:08:29.581405
192	t	Hisar	2025-11-05 10:08:29.582807	\N	\N	Hisar	125001	\N	50.00	Haryana	2025-11-05 10:08:29.582819
193	t	Jhajjar	2025-11-05 10:08:29.584465	\N	\N	Jhajjar	124103	\N	50.00	Haryana	2025-11-05 10:08:29.584474
194	t	Jind	2025-11-05 10:08:29.585825	\N	\N	Jind	126102	\N	50.00	Haryana	2025-11-05 10:08:29.585833
195	t	Kaithal	2025-11-05 10:08:29.586988	\N	\N	Kaithal	136027	\N	50.00	Haryana	2025-11-05 10:08:29.586994
196	t	Karnal	2025-11-05 10:08:29.588076	\N	\N	Karnal	132001	\N	50.00	Haryana	2025-11-05 10:08:29.588084
197	t	Kurukshetra	2025-11-05 10:08:29.589212	\N	\N	Kurukshetra	136118	\N	50.00	Haryana	2025-11-05 10:08:29.589219
198	t	Mahendragarh	2025-11-05 10:08:29.590327	\N	\N	Mahendragarh	123029	\N	50.00	Haryana	2025-11-05 10:08:29.590334
199	t	Nuh	2025-11-05 10:08:29.591431	\N	\N	Nuh	122107	\N	50.00	Haryana	2025-11-05 10:08:29.591438
200	t	Palwal	2025-11-05 10:08:29.592562	\N	\N	Palwal	121102	\N	50.00	Haryana	2025-11-05 10:08:29.592571
201	t	Panchkula	2025-11-05 10:08:29.593726	\N	\N	Panchkula	134109	\N	50.00	Haryana	2025-11-05 10:08:29.593733
202	t	Panipat	2025-11-05 10:08:29.594841	\N	\N	Panipat	132103	\N	50.00	Haryana	2025-11-05 10:08:29.594848
203	t	Rewari	2025-11-05 10:08:29.596021	\N	\N	Rewari	123401	\N	50.00	Haryana	2025-11-05 10:08:29.596027
204	t	Rohtak	2025-11-05 10:08:29.59741	\N	\N	Rohtak	124001	\N	50.00	Haryana	2025-11-05 10:08:29.597417
205	t	Sirsa	2025-11-05 10:08:29.598726	\N	\N	Sirsa	125055	\N	50.00	Haryana	2025-11-05 10:08:29.598733
206	t	Sonipat	2025-11-05 10:08:29.599949	\N	\N	Sonipat	131001	\N	50.00	Haryana	2025-11-05 10:08:29.599955
207	t	Yamunanagar	2025-11-05 10:08:29.601119	\N	\N	Yamunanagar	135001	\N	50.00	Haryana	2025-11-05 10:08:29.601127
208	t	Bilaspur	2025-11-05 10:08:29.602216	\N	\N	Bilaspur	174001	\N	50.00	Himachal Pradesh	2025-11-05 10:08:29.602222
209	t	Chamba	2025-11-05 10:08:29.603292	\N	\N	Chamba	176310	\N	50.00	Himachal Pradesh	2025-11-05 10:08:29.603299
210	t	Hamirpur	2025-11-05 10:08:29.604407	\N	\N	Hamirpur	177001	\N	50.00	Himachal Pradesh	2025-11-05 10:08:29.604414
211	t	Kangra	2025-11-05 10:08:29.605637	\N	\N	Kangra	176001	\N	50.00	Himachal Pradesh	2025-11-05 10:08:29.605644
212	t	Kinnaur	2025-11-05 10:08:29.606717	\N	\N	Kinnaur	172107	\N	50.00	Himachal Pradesh	2025-11-05 10:08:29.606724
213	t	Kullu	2025-11-05 10:08:29.60782	\N	\N	Kullu	175101	\N	50.00	Himachal Pradesh	2025-11-05 10:08:29.607827
214	t	Lahaul and Spiti	2025-11-05 10:08:29.608919	\N	\N	Lahaul and Spiti	175132	\N	50.00	Himachal Pradesh	2025-11-05 10:08:29.608926
215	t	Mandi	2025-11-05 10:08:29.610028	\N	\N	Mandi	175001	\N	50.00	Himachal Pradesh	2025-11-05 10:08:29.610035
216	t	Shimla	2025-11-05 10:08:29.611124	\N	\N	Shimla	171001	\N	50.00	Himachal Pradesh	2025-11-05 10:08:29.61113
217	t	Sirmaur	2025-11-05 10:08:29.612213	\N	\N	Sirmaur	173001	\N	50.00	Himachal Pradesh	2025-11-05 10:08:29.612219
218	t	Solan	2025-11-05 10:08:29.613292	\N	\N	Solan	173212	\N	50.00	Himachal Pradesh	2025-11-05 10:08:29.613298
219	t	Una	2025-11-05 10:08:29.614449	\N	\N	Una	174303	\N	50.00	Himachal Pradesh	2025-11-05 10:08:29.614456
220	t	Anantnag	2025-11-05 10:08:29.615562	\N	\N	Anantnag	192101	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.61557
221	t	Bandipora	2025-11-05 10:08:29.616717	\N	\N	Bandipora	193502	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.616725
222	t	Baramulla	2025-11-05 10:08:29.618067	\N	\N	Baramulla	193101	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.618074
223	t	Budgam	2025-11-05 10:08:29.619236	\N	\N	Budgam	191111	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.619243
224	t	Doda	2025-11-05 10:08:29.62036	\N	\N	Doda	182202	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.620367
225	t	Ganderbal	2025-11-05 10:08:29.621597	\N	\N	Ganderbal	191201	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.621606
226	t	Jammu	2025-11-05 10:08:29.622666	\N	\N	Jammu	180001	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.622673
227	t	Kathua	2025-11-05 10:08:29.623765	\N	\N	Kathua	184101	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.623772
228	t	Kishtwar	2025-11-05 10:08:29.624875	\N	\N	Kishtwar	182204	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.624882
229	t	Kulgam	2025-11-05 10:08:29.626085	\N	\N	Kulgam	192231	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.626092
230	t	Kupwara	2025-11-05 10:08:29.627208	\N	\N	Kupwara	193222	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.627214
231	t	Poonch	2025-11-05 10:08:29.628362	\N	\N	Poonch	185101	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.628368
232	t	Pulwama	2025-11-05 10:08:29.629536	\N	\N	Pulwama	192301	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.629542
233	t	Rajouri	2025-11-05 10:08:29.630666	\N	\N	Rajouri	185131	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.630672
234	t	Ramban	2025-11-05 10:08:29.631748	\N	\N	Ramban	182144	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.631755
235	t	Reasi	2025-11-05 10:08:29.63291	\N	\N	Reasi	182311	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.632917
236	t	Samba	2025-11-05 10:08:29.634083	\N	\N	Samba	184121	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.63409
237	t	Shopian	2025-11-05 10:08:29.635215	\N	\N	Shopian	192303	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.635221
238	t	Srinagar	2025-11-05 10:08:29.636297	\N	\N	Srinagar	190001	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.636304
239	t	Udhampur	2025-11-05 10:08:29.637482	\N	\N	Udhampur	182101	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:29.637488
240	t	Bokaro	2025-11-05 10:08:29.638591	\N	\N	Bokaro	827001	\N	50.00	Jharkhand	2025-11-05 10:08:29.638598
241	t	Chatra	2025-11-05 10:08:29.639665	\N	\N	Chatra	825401	\N	50.00	Jharkhand	2025-11-05 10:08:29.639671
242	t	Deoghar	2025-11-05 10:08:29.640754	\N	\N	Deoghar	814112	\N	50.00	Jharkhand	2025-11-05 10:08:29.640763
243	t	Dhanbad	2025-11-05 10:08:29.641945	\N	\N	Dhanbad	826001	\N	50.00	Jharkhand	2025-11-05 10:08:29.641952
244	t	Dumka	2025-11-05 10:08:29.643134	\N	\N	Dumka	814101	\N	50.00	Jharkhand	2025-11-05 10:08:29.64314
245	t	East Singhbhum	2025-11-05 10:08:29.644257	\N	\N	East Singhbhum	831001	\N	50.00	Jharkhand	2025-11-05 10:08:29.644263
246	t	Garhwa	2025-11-05 10:08:29.645527	\N	\N	Garhwa	822114	\N	50.00	Jharkhand	2025-11-05 10:08:29.645533
247	t	Giridih	2025-11-05 10:08:29.646644	\N	\N	Giridih	815301	\N	50.00	Jharkhand	2025-11-05 10:08:29.64665
248	t	Godda	2025-11-05 10:08:29.647773	\N	\N	Godda	814133	\N	50.00	Jharkhand	2025-11-05 10:08:29.64778
249	t	Gumla	2025-11-05 10:08:29.649022	\N	\N	Gumla	835207	\N	50.00	Jharkhand	2025-11-05 10:08:29.649029
250	t	Hazaribagh	2025-11-05 10:08:29.650245	\N	\N	Hazaribagh	825301	\N	50.00	Jharkhand	2025-11-05 10:08:29.650252
251	t	Jamtara	2025-11-05 10:08:29.651581	\N	\N	Jamtara	815351	\N	50.00	Jharkhand	2025-11-05 10:08:29.651588
252	t	Khunti	2025-11-05 10:08:29.652816	\N	\N	Khunti	835210	\N	50.00	Jharkhand	2025-11-05 10:08:29.652824
253	t	Koderma	2025-11-05 10:08:29.654027	\N	\N	Koderma	825410	\N	50.00	Jharkhand	2025-11-05 10:08:29.654033
254	t	Latehar	2025-11-05 10:08:29.655162	\N	\N	Latehar	829206	\N	50.00	Jharkhand	2025-11-05 10:08:29.655168
255	t	Lohardaga	2025-11-05 10:08:29.656353	\N	\N	Lohardaga	835302	\N	50.00	Jharkhand	2025-11-05 10:08:29.656359
256	t	Pakur	2025-11-05 10:08:29.657641	\N	\N	Pakur	816107	\N	50.00	Jharkhand	2025-11-05 10:08:29.657681
257	t	Palamu	2025-11-05 10:08:29.65914	\N	\N	Palamu	822101	\N	50.00	Jharkhand	2025-11-05 10:08:29.659147
258	t	Ramgarh	2025-11-05 10:08:29.66025	\N	\N	Ramgarh	829122	\N	50.00	Jharkhand	2025-11-05 10:08:29.660256
259	t	Ranchi	2025-11-05 10:08:29.661402	\N	\N	Ranchi	834001	\N	50.00	Jharkhand	2025-11-05 10:08:29.661409
260	t	Sahibganj	2025-11-05 10:08:29.662515	\N	\N	Sahibganj	816109	\N	50.00	Jharkhand	2025-11-05 10:08:29.662521
261	t	Seraikela Kharsawan	2025-11-05 10:08:29.663618	\N	\N	Seraikela Kharsawan	833219	\N	50.00	Jharkhand	2025-11-05 10:08:29.663624
262	t	Simdega	2025-11-05 10:08:29.664761	\N	\N	Simdega	835223	\N	50.00	Jharkhand	2025-11-05 10:08:29.664768
263	t	West Singhbhum	2025-11-05 10:08:29.665813	\N	\N	West Singhbhum	833201	\N	50.00	Jharkhand	2025-11-05 10:08:29.66582
264	t	Bagalkot	2025-11-05 10:08:29.666911	\N	\N	Bagalkot	587101	\N	50.00	Karnataka	2025-11-05 10:08:29.666917
265	t	Ballari	2025-11-05 10:08:29.667976	\N	\N	Ballari	583101	\N	50.00	Karnataka	2025-11-05 10:08:29.667982
266	t	Belagavi	2025-11-05 10:08:29.669045	\N	\N	Belagavi	590001	\N	50.00	Karnataka	2025-11-05 10:08:29.669051
267	t	Bengaluru Rural	2025-11-05 10:08:29.670075	\N	\N	Bengaluru Rural	562123	\N	50.00	Karnataka	2025-11-05 10:08:29.670081
268	t	Bengaluru Urban	2025-11-05 10:08:29.671153	\N	\N	Bengaluru Urban	560001	\N	50.00	Karnataka	2025-11-05 10:08:29.671159
269	t	Bidar	2025-11-05 10:08:29.672198	\N	\N	Bidar	585401	\N	50.00	Karnataka	2025-11-05 10:08:29.672204
270	t	Chamarajanagar	2025-11-05 10:08:29.673294	\N	\N	Chamarajanagar	571313	\N	50.00	Karnataka	2025-11-05 10:08:29.673301
271	t	Chikkaballapur	2025-11-05 10:08:29.674345	\N	\N	Chikkaballapur	562101	\N	50.00	Karnataka	2025-11-05 10:08:29.674351
272	t	Chikkamagaluru	2025-11-05 10:08:29.675453	\N	\N	Chikkamagaluru	577101	\N	50.00	Karnataka	2025-11-05 10:08:29.675461
273	t	Chitradurga	2025-11-05 10:08:29.676537	\N	\N	Chitradurga	577501	\N	50.00	Karnataka	2025-11-05 10:08:29.676544
274	t	Dakshina Kannada	2025-11-05 10:08:29.677639	\N	\N	Dakshina Kannada	575001	\N	50.00	Karnataka	2025-11-05 10:08:29.677646
275	t	Davanagere	2025-11-05 10:08:29.678976	\N	\N	Davanagere	577001	\N	50.00	Karnataka	2025-11-05 10:08:29.678983
276	t	Dharwad	2025-11-05 10:08:29.680203	\N	\N	Dharwad	580001	\N	50.00	Karnataka	2025-11-05 10:08:29.680209
277	t	Gadag	2025-11-05 10:08:29.68128	\N	\N	Gadag	582101	\N	50.00	Karnataka	2025-11-05 10:08:29.681286
278	t	Hassan	2025-11-05 10:08:29.682425	\N	\N	Hassan	573201	\N	50.00	Karnataka	2025-11-05 10:08:29.682432
279	t	Haveri	2025-11-05 10:08:29.683456	\N	\N	Haveri	581110	\N	50.00	Karnataka	2025-11-05 10:08:29.683462
280	t	Kalaburagi	2025-11-05 10:08:29.684474	\N	\N	Kalaburagi	585101	\N	50.00	Karnataka	2025-11-05 10:08:29.684481
281	t	Kodagu	2025-11-05 10:08:29.685523	\N	\N	Kodagu	571201	\N	50.00	Karnataka	2025-11-05 10:08:29.685529
282	t	Kolar	2025-11-05 10:08:29.686539	\N	\N	Kolar	563101	\N	50.00	Karnataka	2025-11-05 10:08:29.686545
283	t	Koppal	2025-11-05 10:08:29.687555	\N	\N	Koppal	583231	\N	50.00	Karnataka	2025-11-05 10:08:29.687561
284	t	Mandya	2025-11-05 10:08:29.688583	\N	\N	Mandya	571401	\N	50.00	Karnataka	2025-11-05 10:08:29.688589
285	t	Mysuru	2025-11-05 10:08:29.689611	\N	\N	Mysuru	570001	\N	50.00	Karnataka	2025-11-05 10:08:29.689617
286	t	Raichur	2025-11-05 10:08:29.690619	\N	\N	Raichur	584101	\N	50.00	Karnataka	2025-11-05 10:08:29.690625
287	t	Ramanagara	2025-11-05 10:08:29.691657	\N	\N	Ramanagara	562159	\N	50.00	Karnataka	2025-11-05 10:08:29.691664
288	t	Shivamogga	2025-11-05 10:08:29.692747	\N	\N	Shivamogga	577201	\N	50.00	Karnataka	2025-11-05 10:08:29.692753
289	t	Tumakuru	2025-11-05 10:08:29.693783	\N	\N	Tumakuru	572101	\N	50.00	Karnataka	2025-11-05 10:08:29.693789
290	t	Udupi	2025-11-05 10:08:29.694801	\N	\N	Udupi	576101	\N	50.00	Karnataka	2025-11-05 10:08:29.694808
291	t	Uttara Kannada	2025-11-05 10:08:29.695811	\N	\N	Uttara Kannada	581301	\N	50.00	Karnataka	2025-11-05 10:08:29.695817
292	t	Vijayapura	2025-11-05 10:08:29.696779	\N	\N	Vijayapura	586101	\N	50.00	Karnataka	2025-11-05 10:08:29.696786
293	t	Yadgir	2025-11-05 10:08:29.697762	\N	\N	Yadgir	585201	\N	50.00	Karnataka	2025-11-05 10:08:29.697769
294	t	Alappuzha	2025-11-05 10:08:29.698721	\N	\N	Alappuzha	688001	\N	50.00	Kerala	2025-11-05 10:08:29.698729
295	t	Ernakulam	2025-11-05 10:08:29.699685	\N	\N	Ernakulam	682001	\N	50.00	Kerala	2025-11-05 10:08:29.699692
296	t	Idukki	2025-11-05 10:08:29.700765	\N	\N	Idukki	685501	\N	50.00	Kerala	2025-11-05 10:08:29.700771
297	t	Kannur	2025-11-05 10:08:29.701837	\N	\N	Kannur	670001	\N	50.00	Kerala	2025-11-05 10:08:29.701844
298	t	Kasaragod	2025-11-05 10:08:29.702872	\N	\N	Kasaragod	671121	\N	50.00	Kerala	2025-11-05 10:08:29.702878
299	t	Kollam	2025-11-05 10:08:29.703935	\N	\N	Kollam	691001	\N	50.00	Kerala	2025-11-05 10:08:29.703941
300	t	Kottayam	2025-11-05 10:08:29.705089	\N	\N	Kottayam	686001	\N	50.00	Kerala	2025-11-05 10:08:29.705096
301	t	Kozhikode	2025-11-05 10:08:29.706227	\N	\N	Kozhikode	673001	\N	50.00	Kerala	2025-11-05 10:08:29.706235
302	t	Malappuram	2025-11-05 10:08:29.707351	\N	\N	Malappuram	676501	\N	50.00	Kerala	2025-11-05 10:08:29.707358
303	t	Palakkad	2025-11-05 10:08:29.70847	\N	\N	Palakkad	678001	\N	50.00	Kerala	2025-11-05 10:08:29.708478
304	t	Pathanamthitta	2025-11-05 10:08:29.709658	\N	\N	Pathanamthitta	689645	\N	50.00	Kerala	2025-11-05 10:08:29.709666
305	t	Thiruvananthapuram	2025-11-05 10:08:29.710833	\N	\N	Thiruvananthapuram	695001	\N	50.00	Kerala	2025-11-05 10:08:29.710841
306	t	Thrissur	2025-11-05 10:08:29.712032	\N	\N	Thrissur	680001	\N	50.00	Kerala	2025-11-05 10:08:29.71204
307	t	Wayanad	2025-11-05 10:08:29.713279	\N	\N	Wayanad	670645	\N	50.00	Kerala	2025-11-05 10:08:29.713288
308	t	Kargil	2025-11-05 10:08:29.714557	\N	\N	Kargil	194103	\N	50.00	Ladakh	2025-11-05 10:08:29.714566
309	t	Leh	2025-11-05 10:08:29.715826	\N	\N	Leh	194101	\N	50.00	Ladakh	2025-11-05 10:08:29.715835
310	t	Lakshadweep	2025-11-05 10:08:29.717044	\N	\N	Lakshadweep	682555	\N	50.00	Lakshadweep	2025-11-05 10:08:29.717052
311	t	Agar Malwa	2025-11-05 10:08:29.718303	\N	\N	Agar Malwa	465441	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.718311
312	t	Alirajpur	2025-11-05 10:08:29.719658	\N	\N	Alirajpur	457887	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.719666
313	t	Anuppur	2025-11-05 10:08:29.720844	\N	\N	Anuppur	484224	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.720852
314	t	Ashoknagar	2025-11-05 10:08:29.721989	\N	\N	Ashoknagar	473331	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.721997
315	t	Balaghat	2025-11-05 10:08:29.723112	\N	\N	Balaghat	481001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.723119
316	t	Barwani	2025-11-05 10:08:29.724205	\N	\N	Barwani	451551	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.724212
317	t	Betul	2025-11-05 10:08:29.7254	\N	\N	Betul	460001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.725408
318	t	Bhind	2025-11-05 10:08:29.726587	\N	\N	Bhind	477001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.726594
319	t	Bhopal	2025-11-05 10:08:29.7277	\N	\N	Bhopal	462001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.727707
320	t	Burhanpur	2025-11-05 10:08:29.728921	\N	\N	Burhanpur	450331	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.72895
321	t	Chhatarpur	2025-11-05 10:08:29.73022	\N	\N	Chhatarpur	471001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.730281
322	t	Chhindwara	2025-11-05 10:08:29.731481	\N	\N	Chhindwara	480001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.731485
323	t	Damoh	2025-11-05 10:08:29.732772	\N	\N	Damoh	470661	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.732776
324	t	Datia	2025-11-05 10:08:29.733977	\N	\N	Datia	475661	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.733982
325	t	Dewas	2025-11-05 10:08:29.73519	\N	\N	Dewas	455001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.735195
326	t	Dhar	2025-11-05 10:08:29.736296	\N	\N	Dhar	454001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.7363
327	t	Dindori	2025-11-05 10:08:29.737446	\N	\N	Dindori	481880	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.73745
328	t	Guna	2025-11-05 10:08:29.738558	\N	\N	Guna	473001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.738562
329	t	Gwalior	2025-11-05 10:08:29.739634	\N	\N	Gwalior	474001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.739638
330	t	Harda	2025-11-05 10:08:29.740709	\N	\N	Harda	461331	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.740713
331	t	Hoshangabad	2025-11-05 10:08:29.741772	\N	\N	Hoshangabad	461001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.741776
332	t	Indore	2025-11-05 10:08:29.742872	\N	\N	Indore	452001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.742876
333	t	Jabalpur	2025-11-05 10:08:29.744096	\N	\N	Jabalpur	482001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.7441
334	t	Jhabua	2025-11-05 10:08:29.745253	\N	\N	Jhabua	457661	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.745257
335	t	Katni	2025-11-05 10:08:29.746418	\N	\N	Katni	483501	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.746422
336	t	Khandwa	2025-11-05 10:08:29.747582	\N	\N	Khandwa	450001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.747586
337	t	Khargone	2025-11-05 10:08:29.748803	\N	\N	Khargone	451001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.748807
338	t	Mandla	2025-11-05 10:08:29.749983	\N	\N	Mandla	481661	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.749987
339	t	Mandsaur	2025-11-05 10:08:29.751126	\N	\N	Mandsaur	458001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.751129
340	t	Morena	2025-11-05 10:08:29.752307	\N	\N	Morena	476001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.752311
341	t	Narsinghpur	2025-11-05 10:08:29.753519	\N	\N	Narsinghpur	487001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.753523
342	t	Neemuch	2025-11-05 10:08:29.754811	\N	\N	Neemuch	458441	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.754854
343	t	Niwari	2025-11-05 10:08:29.756291	\N	\N	Niwari	472442	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.756295
344	t	Panna	2025-11-05 10:08:29.757641	\N	\N	Panna	488001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.757653
345	t	Raisen	2025-11-05 10:08:29.759361	\N	\N	Raisen	464551	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.759365
346	t	Rajgarh	2025-11-05 10:08:29.760517	\N	\N	Rajgarh	465661	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.76052
347	t	Ratlam	2025-11-05 10:08:29.761656	\N	\N	Ratlam	457001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.761659
348	t	Rewa	2025-11-05 10:08:29.762831	\N	\N	Rewa	486001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.762835
349	t	Sagar	2025-11-05 10:08:29.764022	\N	\N	Sagar	470001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.764026
350	t	Satna	2025-11-05 10:08:29.765239	\N	\N	Satna	485001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.765242
351	t	Sehore	2025-11-05 10:08:29.766443	\N	\N	Sehore	466001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.766446
352	t	Seoni	2025-11-05 10:08:29.767554	\N	\N	Seoni	480661	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.767559
353	t	Shahdol	2025-11-05 10:08:29.768615	\N	\N	Shahdol	484001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.768618
354	t	Shajapur	2025-11-05 10:08:29.769697	\N	\N	Shajapur	465001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.7697
355	t	Sheopur	2025-11-05 10:08:29.770666	\N	\N	Sheopur	476337	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.770668
356	t	Shivpuri	2025-11-05 10:08:29.771641	\N	\N	Shivpuri	473551	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.771644
357	t	Sidhi	2025-11-05 10:08:29.772626	\N	\N	Sidhi	486661	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.772629
358	t	Singrauli	2025-11-05 10:08:29.773639	\N	\N	Singrauli	486889	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.773641
359	t	Tikamgarh	2025-11-05 10:08:29.774611	\N	\N	Tikamgarh	472001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.774614
360	t	Ujjain	2025-11-05 10:08:29.775557	\N	\N	Ujjain	456001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.775561
361	t	Umaria	2025-11-05 10:08:29.776447	\N	\N	Umaria	484661	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.776449
362	t	Vidisha	2025-11-05 10:08:29.777362	\N	\N	Vidisha	464001	\N	50.00	Madhya Pradesh	2025-11-05 10:08:29.777365
363	t	Ahmednagar	2025-11-05 10:08:29.778291	\N	\N	Ahmednagar	414001	\N	50.00	Maharashtra	2025-11-05 10:08:29.778294
364	t	Akola	2025-11-05 10:08:29.779295	\N	\N	Akola	444001	\N	50.00	Maharashtra	2025-11-05 10:08:29.779311
365	t	Amravati	2025-11-05 10:08:29.780251	\N	\N	Amravati	444601	\N	50.00	Maharashtra	2025-11-05 10:08:29.780256
366	t	Aurangabad	2025-11-05 10:08:29.781223	\N	\N	Aurangabad	431001	\N	50.00	Maharashtra	2025-11-05 10:08:29.781225
367	t	Beed	2025-11-05 10:08:29.782176	\N	\N	Beed	431122	\N	50.00	Maharashtra	2025-11-05 10:08:29.782179
368	t	Bhandara	2025-11-05 10:08:29.783246	\N	\N	Bhandara	441904	\N	50.00	Maharashtra	2025-11-05 10:08:29.78325
369	t	Buldhana	2025-11-05 10:08:29.784529	\N	\N	Buldhana	443001	\N	50.00	Maharashtra	2025-11-05 10:08:29.784533
370	t	Chandrapur	2025-11-05 10:08:29.785695	\N	\N	Chandrapur	442401	\N	50.00	Maharashtra	2025-11-05 10:08:29.785698
371	t	Dhule	2025-11-05 10:08:29.786793	\N	\N	Dhule	424001	\N	50.00	Maharashtra	2025-11-05 10:08:29.786796
372	t	Gadchiroli	2025-11-05 10:08:29.787767	\N	\N	Gadchiroli	442605	\N	50.00	Maharashtra	2025-11-05 10:08:29.787769
373	t	Gondia	2025-11-05 10:08:29.788783	\N	\N	Gondia	441601	\N	50.00	Maharashtra	2025-11-05 10:08:29.788786
374	t	Hingoli	2025-11-05 10:08:29.789791	\N	\N	Hingoli	431513	\N	50.00	Maharashtra	2025-11-05 10:08:29.789812
375	t	Jalgaon	2025-11-05 10:08:29.790764	\N	\N	Jalgaon	425001	\N	50.00	Maharashtra	2025-11-05 10:08:29.790767
376	t	Jalna	2025-11-05 10:08:29.791846	\N	\N	Jalna	431203	\N	50.00	Maharashtra	2025-11-05 10:08:29.79185
377	t	Kolhapur	2025-11-05 10:08:29.793054	\N	\N	Kolhapur	416001	\N	50.00	Maharashtra	2025-11-05 10:08:29.793058
378	t	Latur	2025-11-05 10:08:29.794421	\N	\N	Latur	413512	\N	50.00	Maharashtra	2025-11-05 10:08:29.794424
379	t	Mumbai City	2025-11-05 10:08:29.795649	\N	\N	Mumbai City	400001	\N	50.00	Maharashtra	2025-11-05 10:08:29.795653
380	t	Mumbai Suburban	2025-11-05 10:08:29.79682	\N	\N	Mumbai Suburban	400069	\N	50.00	Maharashtra	2025-11-05 10:08:29.796823
381	t	Nagpur	2025-11-05 10:08:29.797847	\N	\N	Nagpur	440001	\N	50.00	Maharashtra	2025-11-05 10:08:29.79785
382	t	Nanded	2025-11-05 10:08:29.799105	\N	\N	Nanded	431601	\N	50.00	Maharashtra	2025-11-05 10:08:29.799108
383	t	Nandurbar	2025-11-05 10:08:29.800427	\N	\N	Nandurbar	425412	\N	50.00	Maharashtra	2025-11-05 10:08:29.800431
384	t	Nashik	2025-11-05 10:08:29.801762	\N	\N	Nashik	422001	\N	50.00	Maharashtra	2025-11-05 10:08:29.801765
385	t	Osmanabad	2025-11-05 10:08:29.803251	\N	\N	Osmanabad	413501	\N	50.00	Maharashtra	2025-11-05 10:08:29.803255
386	t	Palghar	2025-11-05 10:08:29.804544	\N	\N	Palghar	401404	\N	50.00	Maharashtra	2025-11-05 10:08:29.804547
387	t	Parbhani	2025-11-05 10:08:29.805813	\N	\N	Parbhani	431401	\N	50.00	Maharashtra	2025-11-05 10:08:29.805817
388	t	Pune	2025-11-05 10:08:29.806958	\N	\N	Pune	411001	\N	50.00	Maharashtra	2025-11-05 10:08:29.806962
389	t	Raigad	2025-11-05 10:08:29.80794	\N	\N	Raigad	402101	\N	50.00	Maharashtra	2025-11-05 10:08:29.807944
390	t	Ratnagiri	2025-11-05 10:08:29.808953	\N	\N	Ratnagiri	415612	\N	50.00	Maharashtra	2025-11-05 10:08:29.808956
391	t	Sangli	2025-11-05 10:08:29.809947	\N	\N	Sangli	416416	\N	50.00	Maharashtra	2025-11-05 10:08:29.80995
392	t	Satara	2025-11-05 10:08:29.810945	\N	\N	Satara	415001	\N	50.00	Maharashtra	2025-11-05 10:08:29.810949
393	t	Sindhudurg	2025-11-05 10:08:29.81192	\N	\N	Sindhudurg	416520	\N	50.00	Maharashtra	2025-11-05 10:08:29.811923
394	t	Solapur	2025-11-05 10:08:29.813062	\N	\N	Solapur	413001	\N	50.00	Maharashtra	2025-11-05 10:08:29.813065
395	t	Thane	2025-11-05 10:08:29.814083	\N	\N	Thane	400601	\N	50.00	Maharashtra	2025-11-05 10:08:29.814086
396	t	Wardha	2025-11-05 10:08:29.815038	\N	\N	Wardha	442001	\N	50.00	Maharashtra	2025-11-05 10:08:29.815041
397	t	Washim	2025-11-05 10:08:29.815996	\N	\N	Washim	444505	\N	50.00	Maharashtra	2025-11-05 10:08:29.815999
398	t	Yavatmal	2025-11-05 10:08:29.816962	\N	\N	Yavatmal	445001	\N	50.00	Maharashtra	2025-11-05 10:08:29.816965
399	t	Bishnupur	2025-11-05 10:08:29.818054	\N	\N	Bishnupur	795126	\N	50.00	Manipur	2025-11-05 10:08:29.818057
400	t	Chandel	2025-11-05 10:08:29.819085	\N	\N	Chandel	795127	\N	50.00	Manipur	2025-11-05 10:08:29.819089
401	t	Churachandpur	2025-11-05 10:08:29.820146	\N	\N	Churachandpur	795128	\N	50.00	Manipur	2025-11-05 10:08:29.820149
402	t	Imphal East	2025-11-05 10:08:29.821132	\N	\N	Imphal East	795001	\N	50.00	Manipur	2025-11-05 10:08:29.821135
403	t	Imphal West	2025-11-05 10:08:29.822092	\N	\N	Imphal West	795001	\N	50.00	Manipur	2025-11-05 10:08:29.822095
404	t	Jiribam	2025-11-05 10:08:29.823009	\N	\N	Jiribam	795116	\N	50.00	Manipur	2025-11-05 10:08:29.823012
405	t	Kakching	2025-11-05 10:08:29.824016	\N	\N	Kakching	795103	\N	50.00	Manipur	2025-11-05 10:08:29.824019
406	t	Kamjong	2025-11-05 10:08:29.825019	\N	\N	Kamjong	795145	\N	50.00	Manipur	2025-11-05 10:08:29.825023
407	t	Kangpokpi	2025-11-05 10:08:29.825985	\N	\N	Kangpokpi	795112	\N	50.00	Manipur	2025-11-05 10:08:29.825989
408	t	Noney	2025-11-05 10:08:29.827044	\N	\N	Noney	795159	\N	50.00	Manipur	2025-11-05 10:08:29.827047
409	t	Pherzawl	2025-11-05 10:08:29.828392	\N	\N	Pherzawl	795006	\N	50.00	Manipur	2025-11-05 10:08:29.828395
410	t	Senapati	2025-11-05 10:08:29.829467	\N	\N	Senapati	795106	\N	50.00	Manipur	2025-11-05 10:08:29.829471
411	t	Tamenglong	2025-11-05 10:08:29.830459	\N	\N	Tamenglong	795141	\N	50.00	Manipur	2025-11-05 10:08:29.830462
412	t	Tengnoupal	2025-11-05 10:08:29.831514	\N	\N	Tengnoupal	795131	\N	50.00	Manipur	2025-11-05 10:08:29.831518
413	t	Thoubal	2025-11-05 10:08:29.832624	\N	\N	Thoubal	795138	\N	50.00	Manipur	2025-11-05 10:08:29.832628
414	t	Ukhrul	2025-11-05 10:08:29.833763	\N	\N	Ukhrul	795142	\N	50.00	Manipur	2025-11-05 10:08:29.833767
415	t	East Garo Hills	2025-11-05 10:08:29.834948	\N	\N	East Garo Hills	794111	\N	50.00	Meghalaya	2025-11-05 10:08:29.834951
416	t	East Jaintia Hills	2025-11-05 10:08:29.835861	\N	\N	East Jaintia Hills	793110	\N	50.00	Meghalaya	2025-11-05 10:08:29.835864
417	t	East Khasi Hills	2025-11-05 10:08:29.836812	\N	\N	East Khasi Hills	793001	\N	50.00	Meghalaya	2025-11-05 10:08:29.836815
418	t	Mawkyrwat	2025-11-05 10:08:29.83772	\N	\N	Mawkyrwat	793114	\N	50.00	Meghalaya	2025-11-05 10:08:29.837723
419	t	North Garo Hills	2025-11-05 10:08:29.838631	\N	\N	North Garo Hills	794108	\N	50.00	Meghalaya	2025-11-05 10:08:29.838634
420	t	Ri Bhoi	2025-11-05 10:08:29.839573	\N	\N	Ri Bhoi	793101	\N	50.00	Meghalaya	2025-11-05 10:08:29.839576
421	t	South Garo Hills	2025-11-05 10:08:29.8405	\N	\N	South Garo Hills	794102	\N	50.00	Meghalaya	2025-11-05 10:08:29.840503
422	t	South West Garo Hills	2025-11-05 10:08:29.841551	\N	\N	South West Garo Hills	794105	\N	50.00	Meghalaya	2025-11-05 10:08:29.841554
423	t	South West Khasi Hills	2025-11-05 10:08:29.842552	\N	\N	South West Khasi Hills	793119	\N	50.00	Meghalaya	2025-11-05 10:08:29.842555
424	t	West Garo Hills	2025-11-05 10:08:29.843509	\N	\N	West Garo Hills	794101	\N	50.00	Meghalaya	2025-11-05 10:08:29.843517
425	t	West Jaintia Hills	2025-11-05 10:08:29.844499	\N	\N	West Jaintia Hills	793109	\N	50.00	Meghalaya	2025-11-05 10:08:29.844502
426	t	West Khasi Hills	2025-11-05 10:08:29.845491	\N	\N	West Khasi Hills	793106	\N	50.00	Meghalaya	2025-11-05 10:08:29.845494
427	t	Aizawl	2025-11-05 10:08:29.846451	\N	\N	Aizawl	796001	\N	50.00	Mizoram	2025-11-05 10:08:29.846455
428	t	Champhai	2025-11-05 10:08:29.847408	\N	\N	Champhai	796321	\N	50.00	Mizoram	2025-11-05 10:08:29.847411
429	t	Hnahthial	2025-11-05 10:08:29.848488	\N	\N	Hnahthial	796571	\N	50.00	Mizoram	2025-11-05 10:08:29.848491
430	t	Khawzawl	2025-11-05 10:08:29.849575	\N	\N	Khawzawl	796310	\N	50.00	Mizoram	2025-11-05 10:08:29.849578
431	t	Kolasib	2025-11-05 10:08:29.85053	\N	\N	Kolasib	796070	\N	50.00	Mizoram	2025-11-05 10:08:29.850533
432	t	Lawngtlai	2025-11-05 10:08:29.85158	\N	\N	Lawngtlai	796891	\N	50.00	Mizoram	2025-11-05 10:08:29.851583
433	t	Lunglei	2025-11-05 10:08:29.852527	\N	\N	Lunglei	796701	\N	50.00	Mizoram	2025-11-05 10:08:29.85253
434	t	Mamit	2025-11-05 10:08:29.853505	\N	\N	Mamit	796441	\N	50.00	Mizoram	2025-11-05 10:08:29.853508
435	t	Saiha	2025-11-05 10:08:29.85442	\N	\N	Saiha	796901	\N	50.00	Mizoram	2025-11-05 10:08:29.854423
436	t	Saitual	2025-11-05 10:08:29.855391	\N	\N	Saitual	796261	\N	50.00	Mizoram	2025-11-05 10:08:29.855394
437	t	Serchhip	2025-11-05 10:08:29.856355	\N	\N	Serchhip	796181	\N	50.00	Mizoram	2025-11-05 10:08:29.856358
438	t	Chumukedima	2025-11-05 10:08:29.857335	\N	\N	Chumukedima	797103	\N	50.00	Nagaland	2025-11-05 10:08:29.857338
439	t	Dimapur	2025-11-05 10:08:29.858272	\N	\N	Dimapur	797112	\N	50.00	Nagaland	2025-11-05 10:08:29.858275
440	t	Kiphire	2025-11-05 10:08:29.859241	\N	\N	Kiphire	798611	\N	50.00	Nagaland	2025-11-05 10:08:29.859244
441	t	Kohima	2025-11-05 10:08:29.86028	\N	\N	Kohima	797001	\N	50.00	Nagaland	2025-11-05 10:08:29.860283
442	t	Longleng	2025-11-05 10:08:29.861248	\N	\N	Longleng	798625	\N	50.00	Nagaland	2025-11-05 10:08:29.861251
443	t	Mokokchung	2025-11-05 10:08:29.862259	\N	\N	Mokokchung	798601	\N	50.00	Nagaland	2025-11-05 10:08:29.862262
444	t	Mon	2025-11-05 10:08:29.863301	\N	\N	Mon	798621	\N	50.00	Nagaland	2025-11-05 10:08:29.863304
445	t	Niuland	2025-11-05 10:08:29.864312	\N	\N	Niuland	797105	\N	50.00	Nagaland	2025-11-05 10:08:29.864315
446	t	Noklak	2025-11-05 10:08:29.865304	\N	\N	Noklak	798626	\N	50.00	Nagaland	2025-11-05 10:08:29.865307
447	t	Peren	2025-11-05 10:08:29.866249	\N	\N	Peren	797101	\N	50.00	Nagaland	2025-11-05 10:08:29.866253
448	t	Phek	2025-11-05 10:08:29.867212	\N	\N	Phek	797108	\N	50.00	Nagaland	2025-11-05 10:08:29.867215
449	t	Shamator	2025-11-05 10:08:29.868332	\N	\N	Shamator	798612	\N	50.00	Nagaland	2025-11-05 10:08:29.868336
450	t	Tseminu	2025-11-05 10:08:29.869492	\N	\N	Tseminu	797109	\N	50.00	Nagaland	2025-11-05 10:08:29.869495
451	t	Tuensang	2025-11-05 10:08:29.87061	\N	\N	Tuensang	798612	\N	50.00	Nagaland	2025-11-05 10:08:29.870614
452	t	Wokha	2025-11-05 10:08:29.871763	\N	\N	Wokha	797111	\N	50.00	Nagaland	2025-11-05 10:08:29.871767
453	t	Zunheboto	2025-11-05 10:08:29.872985	\N	\N	Zunheboto	798620	\N	50.00	Nagaland	2025-11-05 10:08:29.872989
454	t	Angul	2025-11-05 10:08:29.874188	\N	\N	Angul	759122	\N	50.00	Odisha	2025-11-05 10:08:29.874192
455	t	Balangir	2025-11-05 10:08:29.875357	\N	\N	Balangir	767001	\N	50.00	Odisha	2025-11-05 10:08:29.87536
456	t	Balasore	2025-11-05 10:08:29.876301	\N	\N	Balasore	756001	\N	50.00	Odisha	2025-11-05 10:08:29.876306
457	t	Bargarh	2025-11-05 10:08:29.877268	\N	\N	Bargarh	768028	\N	50.00	Odisha	2025-11-05 10:08:29.877271
458	t	Bhadrak	2025-11-05 10:08:29.878213	\N	\N	Bhadrak	756100	\N	50.00	Odisha	2025-11-05 10:08:29.878216
459	t	Boudh	2025-11-05 10:08:29.879147	\N	\N	Boudh	762014	\N	50.00	Odisha	2025-11-05 10:08:29.87915
460	t	Cuttack	2025-11-05 10:08:29.880076	\N	\N	Cuttack	753001	\N	50.00	Odisha	2025-11-05 10:08:29.880079
461	t	Deogarh	2025-11-05 10:08:29.881031	\N	\N	Deogarh	768108	\N	50.00	Odisha	2025-11-05 10:08:29.881035
462	t	Dhenkanal	2025-11-05 10:08:29.881968	\N	\N	Dhenkanal	759001	\N	50.00	Odisha	2025-11-05 10:08:29.881971
463	t	Gajapati	2025-11-05 10:08:29.883005	\N	\N	Gajapati	761200	\N	50.00	Odisha	2025-11-05 10:08:29.88301
464	t	Ganjam	2025-11-05 10:08:29.884206	\N	\N	Ganjam	761026	\N	50.00	Odisha	2025-11-05 10:08:29.884209
465	t	Jagatsinghpur	2025-11-05 10:08:29.885436	\N	\N	Jagatsinghpur	754103	\N	50.00	Odisha	2025-11-05 10:08:29.88545
466	t	Jajpur	2025-11-05 10:08:29.886588	\N	\N	Jajpur	755001	\N	50.00	Odisha	2025-11-05 10:08:29.886607
467	t	Jharsuguda	2025-11-05 10:08:29.887666	\N	\N	Jharsuguda	768201	\N	50.00	Odisha	2025-11-05 10:08:29.887669
468	t	Kalahandi	2025-11-05 10:08:29.888631	\N	\N	Kalahandi	766001	\N	50.00	Odisha	2025-11-05 10:08:29.888634
469	t	Kandhamal	2025-11-05 10:08:29.889615	\N	\N	Kandhamal	762001	\N	50.00	Odisha	2025-11-05 10:08:29.889618
470	t	Kendrapara	2025-11-05 10:08:29.890589	\N	\N	Kendrapara	754211	\N	50.00	Odisha	2025-11-05 10:08:29.890592
471	t	Kendujhar	2025-11-05 10:08:29.891636	\N	\N	Kendujhar	758001	\N	50.00	Odisha	2025-11-05 10:08:29.891639
472	t	Khordha	2025-11-05 10:08:29.892641	\N	\N	Khordha	751001	\N	50.00	Odisha	2025-11-05 10:08:29.892644
473	t	Koraput	2025-11-05 10:08:29.893598	\N	\N	Koraput	764001	\N	50.00	Odisha	2025-11-05 10:08:29.893602
474	t	Malkangiri	2025-11-05 10:08:29.894597	\N	\N	Malkangiri	764045	\N	50.00	Odisha	2025-11-05 10:08:29.894601
475	t	Mayurbhanj	2025-11-05 10:08:29.895729	\N	\N	Mayurbhanj	757001	\N	50.00	Odisha	2025-11-05 10:08:29.895733
476	t	Nabarangpur	2025-11-05 10:08:29.896733	\N	\N	Nabarangpur	764059	\N	50.00	Odisha	2025-11-05 10:08:29.896736
477	t	Nayagarh	2025-11-05 10:08:29.897798	\N	\N	Nayagarh	752069	\N	50.00	Odisha	2025-11-05 10:08:29.897802
478	t	Nuapada	2025-11-05 10:08:29.898837	\N	\N	Nuapada	766105	\N	50.00	Odisha	2025-11-05 10:08:29.89884
479	t	Puri	2025-11-05 10:08:29.899745	\N	\N	Puri	752001	\N	50.00	Odisha	2025-11-05 10:08:29.899749
480	t	Rayagada	2025-11-05 10:08:29.900572	\N	\N	Rayagada	765001	\N	50.00	Odisha	2025-11-05 10:08:29.900575
481	t	Sambalpur	2025-11-05 10:08:29.901471	\N	\N	Sambalpur	768001	\N	50.00	Odisha	2025-11-05 10:08:29.901473
482	t	Subarnapur	2025-11-05 10:08:29.902321	\N	\N	Subarnapur	767017	\N	50.00	Odisha	2025-11-05 10:08:29.902324
483	t	Sundargarh	2025-11-05 10:08:29.903184	\N	\N	Sundargarh	770001	\N	50.00	Odisha	2025-11-05 10:08:29.903187
484	t	Karaikal	2025-11-05 10:08:29.904106	\N	\N	Karaikal	609602	\N	50.00	Puducherry	2025-11-05 10:08:29.904109
485	t	Mahe	2025-11-05 10:08:29.904991	\N	\N	Mahe	673310	\N	50.00	Puducherry	2025-11-05 10:08:29.904994
486	t	Puducherry	2025-11-05 10:08:29.905929	\N	\N	Puducherry	605001	\N	50.00	Puducherry	2025-11-05 10:08:29.905931
487	t	Yanam	2025-11-05 10:08:29.906818	\N	\N	Yanam	533464	\N	50.00	Puducherry	2025-11-05 10:08:29.906821
488	t	Amritsar	2025-11-05 10:08:29.907698	\N	\N	Amritsar	143001	\N	50.00	Punjab	2025-11-05 10:08:29.907701
489	t	Barnala	2025-11-05 10:08:29.908547	\N	\N	Barnala	148101	\N	50.00	Punjab	2025-11-05 10:08:29.90855
490	t	Bathinda	2025-11-05 10:08:29.9095	\N	\N	Bathinda	151001	\N	50.00	Punjab	2025-11-05 10:08:29.909504
491	t	Faridkot	2025-11-05 10:08:29.910476	\N	\N	Faridkot	151203	\N	50.00	Punjab	2025-11-05 10:08:29.910479
492	t	Fatehgarh Sahib	2025-11-05 10:08:29.911368	\N	\N	Fatehgarh Sahib	140406	\N	50.00	Punjab	2025-11-05 10:08:29.911371
493	t	Fazilka	2025-11-05 10:08:29.912322	\N	\N	Fazilka	152123	\N	50.00	Punjab	2025-11-05 10:08:29.912325
494	t	Ferozepur	2025-11-05 10:08:29.913303	\N	\N	Ferozepur	152001	\N	50.00	Punjab	2025-11-05 10:08:29.913307
495	t	Gurdaspur	2025-11-05 10:08:29.914273	\N	\N	Gurdaspur	143521	\N	50.00	Punjab	2025-11-05 10:08:29.914276
496	t	Hoshiarpur	2025-11-05 10:08:29.915241	\N	\N	Hoshiarpur	146001	\N	50.00	Punjab	2025-11-05 10:08:29.915244
497	t	Jalandhar	2025-11-05 10:08:29.916184	\N	\N	Jalandhar	144001	\N	50.00	Punjab	2025-11-05 10:08:29.916187
498	t	Kapurthala	2025-11-05 10:08:29.917152	\N	\N	Kapurthala	144601	\N	50.00	Punjab	2025-11-05 10:08:29.917156
499	t	Ludhiana	2025-11-05 10:08:29.918062	\N	\N	Ludhiana	141001	\N	50.00	Punjab	2025-11-05 10:08:29.918065
500	t	Malerkotla	2025-11-05 10:08:29.919011	\N	\N	Malerkotla	148023	\N	50.00	Punjab	2025-11-05 10:08:29.919014
501	t	Mansa	2025-11-05 10:08:29.920036	\N	\N	Mansa	151505	\N	50.00	Punjab	2025-11-05 10:08:29.920039
502	t	Moga	2025-11-05 10:08:29.921112	\N	\N	Moga	142001	\N	50.00	Punjab	2025-11-05 10:08:29.921116
503	t	Muktsar	2025-11-05 10:08:29.922018	\N	\N	Muktsar	152026	\N	50.00	Punjab	2025-11-05 10:08:29.922021
504	t	Nawanshahr	2025-11-05 10:08:29.922999	\N	\N	Nawanshahr	144514	\N	50.00	Punjab	2025-11-05 10:08:29.923002
505	t	Pathankot	2025-11-05 10:08:29.923974	\N	\N	Pathankot	145001	\N	50.00	Punjab	2025-11-05 10:08:29.923977
506	t	Patiala	2025-11-05 10:08:29.925219	\N	\N	Patiala	147001	\N	50.00	Punjab	2025-11-05 10:08:29.925223
507	t	Rupnagar	2025-11-05 10:08:29.926303	\N	\N	Rupnagar	140001	\N	50.00	Punjab	2025-11-05 10:08:29.926306
508	t	Sangrur	2025-11-05 10:08:29.927428	\N	\N	Sangrur	148001	\N	50.00	Punjab	2025-11-05 10:08:29.927431
509	t	SAS Nagar	2025-11-05 10:08:29.928683	\N	\N	SAS Nagar	160055	\N	50.00	Punjab	2025-11-05 10:08:29.928686
510	t	Tarn Taran	2025-11-05 10:08:29.929792	\N	\N	Tarn Taran	143401	\N	50.00	Punjab	2025-11-05 10:08:29.929796
511	t	Ajmer	2025-11-05 10:08:29.931571	\N	\N	Ajmer	305001	\N	50.00	Rajasthan	2025-11-05 10:08:29.931575
512	t	Alwar	2025-11-05 10:08:29.934396	\N	\N	Alwar	301001	\N	50.00	Rajasthan	2025-11-05 10:08:29.934403
513	t	Banswara	2025-11-05 10:08:29.937168	\N	\N	Banswara	327001	\N	50.00	Rajasthan	2025-11-05 10:08:29.937172
514	t	Baran	2025-11-05 10:08:29.938464	\N	\N	Baran	325205	\N	50.00	Rajasthan	2025-11-05 10:08:29.938468
515	t	Barmer	2025-11-05 10:08:29.939724	\N	\N	Barmer	344001	\N	50.00	Rajasthan	2025-11-05 10:08:29.939728
516	t	Bharatpur	2025-11-05 10:08:29.940896	\N	\N	Bharatpur	321001	\N	50.00	Rajasthan	2025-11-05 10:08:29.9409
517	t	Bhilwara	2025-11-05 10:08:29.942035	\N	\N	Bhilwara	311001	\N	50.00	Rajasthan	2025-11-05 10:08:29.942038
518	t	Bikaner	2025-11-05 10:08:29.943112	\N	\N	Bikaner	334001	\N	50.00	Rajasthan	2025-11-05 10:08:29.943115
519	t	Bundi	2025-11-05 10:08:29.944121	\N	\N	Bundi	323001	\N	50.00	Rajasthan	2025-11-05 10:08:29.944125
520	t	Chittorgarh	2025-11-05 10:08:29.945056	\N	\N	Chittorgarh	312001	\N	50.00	Rajasthan	2025-11-05 10:08:29.945058
521	t	Churu	2025-11-05 10:08:29.946033	\N	\N	Churu	331001	\N	50.00	Rajasthan	2025-11-05 10:08:29.946036
522	t	Dausa	2025-11-05 10:08:29.946914	\N	\N	Dausa	303303	\N	50.00	Rajasthan	2025-11-05 10:08:29.946917
523	t	Dholpur	2025-11-05 10:08:29.947754	\N	\N	Dholpur	328001	\N	50.00	Rajasthan	2025-11-05 10:08:29.947757
524	t	Dungarpur	2025-11-05 10:08:29.948643	\N	\N	Dungarpur	314001	\N	50.00	Rajasthan	2025-11-05 10:08:29.948646
525	t	Hanumangarh	2025-11-05 10:08:29.949591	\N	\N	Hanumangarh	335512	\N	50.00	Rajasthan	2025-11-05 10:08:29.949593
526	t	Jaipur	2025-11-05 10:08:29.950533	\N	\N	Jaipur	302001	\N	50.00	Rajasthan	2025-11-05 10:08:29.950535
527	t	Jaisalmer	2025-11-05 10:08:29.951426	\N	\N	Jaisalmer	345001	\N	50.00	Rajasthan	2025-11-05 10:08:29.951429
528	t	Jalore	2025-11-05 10:08:29.952348	\N	\N	Jalore	343001	\N	50.00	Rajasthan	2025-11-05 10:08:29.952351
529	t	Jhalawar	2025-11-05 10:08:29.953771	\N	\N	Jhalawar	326001	\N	50.00	Rajasthan	2025-11-05 10:08:29.953774
530	t	Jhunjhunu	2025-11-05 10:08:29.954735	\N	\N	Jhunjhunu	333001	\N	50.00	Rajasthan	2025-11-05 10:08:29.954738
531	t	Jodhpur	2025-11-05 10:08:29.955673	\N	\N	Jodhpur	342001	\N	50.00	Rajasthan	2025-11-05 10:08:29.955676
532	t	Karauli	2025-11-05 10:08:29.956613	\N	\N	Karauli	322241	\N	50.00	Rajasthan	2025-11-05 10:08:29.956616
533	t	Kota	2025-11-05 10:08:29.957707	\N	\N	Kota	324001	\N	50.00	Rajasthan	2025-11-05 10:08:29.95771
534	t	Nagaur	2025-11-05 10:08:29.958779	\N	\N	Nagaur	341001	\N	50.00	Rajasthan	2025-11-05 10:08:29.958783
535	t	Pali	2025-11-05 10:08:29.960014	\N	\N	Pali	306401	\N	50.00	Rajasthan	2025-11-05 10:08:29.960018
536	t	Pratapgarh	2025-11-05 10:08:29.961029	\N	\N	Pratapgarh	312605	\N	50.00	Rajasthan	2025-11-05 10:08:29.961032
537	t	Rajsamand	2025-11-05 10:08:29.962042	\N	\N	Rajsamand	313324	\N	50.00	Rajasthan	2025-11-05 10:08:29.962046
538	t	Sawai Madhopur	2025-11-05 10:08:29.963151	\N	\N	Sawai Madhopur	322001	\N	50.00	Rajasthan	2025-11-05 10:08:29.963155
539	t	Sikar	2025-11-05 10:08:29.964219	\N	\N	Sikar	332001	\N	50.00	Rajasthan	2025-11-05 10:08:29.964222
540	t	Sirohi	2025-11-05 10:08:29.965184	\N	\N	Sirohi	307001	\N	50.00	Rajasthan	2025-11-05 10:08:29.965188
541	t	Sri Ganganagar	2025-11-05 10:08:29.966261	\N	\N	Sri Ganganagar	335001	\N	50.00	Rajasthan	2025-11-05 10:08:29.966264
542	t	Tonk	2025-11-05 10:08:29.967386	\N	\N	Tonk	304001	\N	50.00	Rajasthan	2025-11-05 10:08:29.967389
543	t	Udaipur	2025-11-05 10:08:29.968502	\N	\N	Udaipur	313001	\N	50.00	Rajasthan	2025-11-05 10:08:29.968505
544	t	East Sikkim	2025-11-05 10:08:29.969539	\N	\N	East Sikkim	737101	\N	50.00	Sikkim	2025-11-05 10:08:29.969543
545	t	North Sikkim	2025-11-05 10:08:29.970522	\N	\N	North Sikkim	737116	\N	50.00	Sikkim	2025-11-05 10:08:29.970526
546	t	South Sikkim	2025-11-05 10:08:29.971453	\N	\N	South Sikkim	737126	\N	50.00	Sikkim	2025-11-05 10:08:29.971456
547	t	West Sikkim	2025-11-05 10:08:29.972344	\N	\N	West Sikkim	737111	\N	50.00	Sikkim	2025-11-05 10:08:29.972347
548	t	Ariyalur	2025-11-05 10:08:29.973291	\N	\N	Ariyalur	621704	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.973294
549	t	Chennai	2025-11-05 10:08:29.974362	\N	\N	Chennai	600001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.974365
550	t	Coimbatore	2025-11-05 10:08:29.975343	\N	\N	Coimbatore	641001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.975345
551	t	Cuddalore	2025-11-05 10:08:29.976195	\N	\N	Cuddalore	607001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.976198
552	t	Dharmapuri	2025-11-05 10:08:29.977049	\N	\N	Dharmapuri	636701	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.977052
553	t	Dindigul	2025-11-05 10:08:29.977942	\N	\N	Dindigul	624001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.977945
554	t	Erode	2025-11-05 10:08:29.978847	\N	\N	Erode	638001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.97885
555	t	Kallakurichi	2025-11-05 10:08:29.979772	\N	\N	Kallakurichi	606202	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.979775
556	t	Kanchipuram	2025-11-05 10:08:29.980726	\N	\N	Kanchipuram	631501	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.980729
557	t	Kanyakumari	2025-11-05 10:08:29.981681	\N	\N	Kanyakumari	629001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.981684
558	t	Karur	2025-11-05 10:08:29.982724	\N	\N	Karur	639001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.982727
559	t	Krishnagiri	2025-11-05 10:08:29.98362	\N	\N	Krishnagiri	635001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.983623
560	t	Madurai	2025-11-05 10:08:29.984518	\N	\N	Madurai	625001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.984521
561	t	Mayiladuthurai	2025-11-05 10:08:29.98546	\N	\N	Mayiladuthurai	609001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.985463
562	t	Nagapattinam	2025-11-05 10:08:29.986355	\N	\N	Nagapattinam	611001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.986358
563	t	Namakkal	2025-11-05 10:08:29.98734	\N	\N	Namakkal	637001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.987343
564	t	Nilgiris	2025-11-05 10:08:29.988263	\N	\N	Nilgiris	643001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.988265
565	t	Perambalur	2025-11-05 10:08:29.989272	\N	\N	Perambalur	621212	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.989275
566	t	Pudukkottai	2025-11-05 10:08:29.990176	\N	\N	Pudukkottai	622001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.990179
567	t	Ramanathapuram	2025-11-05 10:08:29.991079	\N	\N	Ramanathapuram	623501	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.991082
568	t	Ranipet	2025-11-05 10:08:29.991909	\N	\N	Ranipet	632401	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.991913
569	t	Salem	2025-11-05 10:08:29.99273	\N	\N	Salem	636001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.992733
570	t	Sivaganga	2025-11-05 10:08:29.993626	\N	\N	Sivaganga	630561	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.993628
571	t	Tenkasi	2025-11-05 10:08:29.994536	\N	\N	Tenkasi	627811	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.994539
572	t	Thanjavur	2025-11-05 10:08:29.995612	\N	\N	Thanjavur	613001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.995615
573	t	Theni	2025-11-05 10:08:29.996552	\N	\N	Theni	625531	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.996555
574	t	Thoothukudi	2025-11-05 10:08:29.997492	\N	\N	Thoothukudi	628001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.997496
575	t	Tiruchirappalli	2025-11-05 10:08:29.99843	\N	\N	Tiruchirappalli	620001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.998434
576	t	Tirunelveli	2025-11-05 10:08:29.999928	\N	\N	Tirunelveli	627001	\N	50.00	Tamil Nadu	2025-11-05 10:08:29.999932
577	t	Tirupathur	2025-11-05 10:08:30.001211	\N	\N	Tirupathur	635601	\N	50.00	Tamil Nadu	2025-11-05 10:08:30.001215
578	t	Tiruppur	2025-11-05 10:08:30.002425	\N	\N	Tiruppur	641601	\N	50.00	Tamil Nadu	2025-11-05 10:08:30.002429
579	t	Tiruvallur	2025-11-05 10:08:30.003531	\N	\N	Tiruvallur	602001	\N	50.00	Tamil Nadu	2025-11-05 10:08:30.003534
580	t	Tiruvannamalai	2025-11-05 10:08:30.004485	\N	\N	Tiruvannamalai	606601	\N	50.00	Tamil Nadu	2025-11-05 10:08:30.004488
581	t	Vellore	2025-11-05 10:08:30.005413	\N	\N	Vellore	632001	\N	50.00	Tamil Nadu	2025-11-05 10:08:30.005415
582	t	Viluppuram	2025-11-05 10:08:30.006285	\N	\N	Viluppuram	605602	\N	50.00	Tamil Nadu	2025-11-05 10:08:30.006288
583	t	Virudhunagar	2025-11-05 10:08:30.007139	\N	\N	Virudhunagar	626001	\N	50.00	Tamil Nadu	2025-11-05 10:08:30.007142
584	t	Adilabad	2025-11-05 10:08:30.008003	\N	\N	Adilabad	504001	\N	50.00	Telangana	2025-11-05 10:08:30.008005
585	t	Bhadradri Kothagudem	2025-11-05 10:08:30.008878	\N	\N	Bhadradri Kothagudem	507101	\N	50.00	Telangana	2025-11-05 10:08:30.008881
586	t	Hyderabad	2025-11-05 10:08:30.009809	\N	\N	Hyderabad	500001	\N	50.00	Telangana	2025-11-05 10:08:30.009812
587	t	Jagtial	2025-11-05 10:08:30.010696	\N	\N	Jagtial	505327	\N	50.00	Telangana	2025-11-05 10:08:30.010699
588	t	Jangaon	2025-11-05 10:08:30.011547	\N	\N	Jangaon	506167	\N	50.00	Telangana	2025-11-05 10:08:30.01155
589	t	Jayashankar Bhupalpally	2025-11-05 10:08:30.012468	\N	\N	Jayashankar Bhupalpally	506169	\N	50.00	Telangana	2025-11-05 10:08:30.012471
590	t	Jogulamba Gadwal	2025-11-05 10:08:30.013396	\N	\N	Jogulamba Gadwal	509125	\N	50.00	Telangana	2025-11-05 10:08:30.013399
591	t	Kamareddy	2025-11-05 10:08:30.014307	\N	\N	Kamareddy	503111	\N	50.00	Telangana	2025-11-05 10:08:30.01431
592	t	Karimnagar	2025-11-05 10:08:30.015197	\N	\N	Karimnagar	505001	\N	50.00	Telangana	2025-11-05 10:08:30.0152
593	t	Khammam	2025-11-05 10:08:30.01616	\N	\N	Khammam	507001	\N	50.00	Telangana	2025-11-05 10:08:30.016162
594	t	Komaram Bheem	2025-11-05 10:08:30.017014	\N	\N	Komaram Bheem	504293	\N	50.00	Telangana	2025-11-05 10:08:30.017017
595	t	Mahabubabad	2025-11-05 10:08:30.017934	\N	\N	Mahabubabad	506101	\N	50.00	Telangana	2025-11-05 10:08:30.017937
596	t	Mahabubnagar	2025-11-05 10:08:30.018918	\N	\N	Mahabubnagar	509001	\N	50.00	Telangana	2025-11-05 10:08:30.018921
597	t	Mancherial	2025-11-05 10:08:30.019906	\N	\N	Mancherial	504208	\N	50.00	Telangana	2025-11-05 10:08:30.01991
598	t	Medak	2025-11-05 10:08:30.020868	\N	\N	Medak	502110	\N	50.00	Telangana	2025-11-05 10:08:30.020871
599	t	Medchal Malkajgiri	2025-11-05 10:08:30.022541	\N	\N	Medchal Malkajgiri	500014	\N	50.00	Telangana	2025-11-05 10:08:30.022543
600	t	Mulugu	2025-11-05 10:08:30.023503	\N	\N	Mulugu	506343	\N	50.00	Telangana	2025-11-05 10:08:30.023507
601	t	Nagarkurnool	2025-11-05 10:08:30.024431	\N	\N	Nagarkurnool	509209	\N	50.00	Telangana	2025-11-05 10:08:30.024434
602	t	Nalgonda	2025-11-05 10:08:30.025341	\N	\N	Nalgonda	508001	\N	50.00	Telangana	2025-11-05 10:08:30.025343
603	t	Narayanpet	2025-11-05 10:08:30.026178	\N	\N	Narayanpet	509210	\N	50.00	Telangana	2025-11-05 10:08:30.026181
604	t	Nirmal	2025-11-05 10:08:30.026985	\N	\N	Nirmal	504106	\N	50.00	Telangana	2025-11-05 10:08:30.026988
605	t	Nizamabad	2025-11-05 10:08:30.027909	\N	\N	Nizamabad	503001	\N	50.00	Telangana	2025-11-05 10:08:30.027913
606	t	Peddapalli	2025-11-05 10:08:30.028808	\N	\N	Peddapalli	505172	\N	50.00	Telangana	2025-11-05 10:08:30.028811
607	t	Rajanna Sircilla	2025-11-05 10:08:30.02969	\N	\N	Rajanna Sircilla	505301	\N	50.00	Telangana	2025-11-05 10:08:30.029693
608	t	Ranga Reddy	2025-11-05 10:08:30.03057	\N	\N	Ranga Reddy	500032	\N	50.00	Telangana	2025-11-05 10:08:30.030572
609	t	Sangareddy	2025-11-05 10:08:30.031553	\N	\N	Sangareddy	502001	\N	50.00	Telangana	2025-11-05 10:08:30.031556
610	t	Siddipet	2025-11-05 10:08:30.032407	\N	\N	Siddipet	502103	\N	50.00	Telangana	2025-11-05 10:08:30.032409
611	t	Suryapet	2025-11-05 10:08:30.03332	\N	\N	Suryapet	508213	\N	50.00	Telangana	2025-11-05 10:08:30.033323
612	t	Vikarabad	2025-11-05 10:08:30.034169	\N	\N	Vikarabad	501101	\N	50.00	Telangana	2025-11-05 10:08:30.034172
613	t	Wanaparthy	2025-11-05 10:08:30.035108	\N	\N	Wanaparthy	509103	\N	50.00	Telangana	2025-11-05 10:08:30.035111
614	t	Warangal Rural	2025-11-05 10:08:30.036083	\N	\N	Warangal Rural	506144	\N	50.00	Telangana	2025-11-05 10:08:30.036086
615	t	Warangal Urban	2025-11-05 10:08:30.036978	\N	\N	Warangal Urban	506002	\N	50.00	Telangana	2025-11-05 10:08:30.036981
616	t	Yadadri Bhuvanagiri	2025-11-05 10:08:30.037902	\N	\N	Yadadri Bhuvanagiri	508116	\N	50.00	Telangana	2025-11-05 10:08:30.037906
617	t	Dhalai	2025-11-05 10:08:30.038811	\N	\N	Dhalai	799278	\N	50.00	Tripura	2025-11-05 10:08:30.038814
618	t	Gomati	2025-11-05 10:08:30.039688	\N	\N	Gomati	799101	\N	50.00	Tripura	2025-11-05 10:08:30.039691
619	t	Khowai	2025-11-05 10:08:30.040532	\N	\N	Khowai	799202	\N	50.00	Tripura	2025-11-05 10:08:30.040536
620	t	North Tripura	2025-11-05 10:08:30.041432	\N	\N	North Tripura	799251	\N	50.00	Tripura	2025-11-05 10:08:30.041435
621	t	Sepahijala	2025-11-05 10:08:30.042324	\N	\N	Sepahijala	799102	\N	50.00	Tripura	2025-11-05 10:08:30.042327
622	t	South Tripura	2025-11-05 10:08:30.043262	\N	\N	South Tripura	799155	\N	50.00	Tripura	2025-11-05 10:08:30.043266
623	t	Unakoti	2025-11-05 10:08:30.044124	\N	\N	Unakoti	799264	\N	50.00	Tripura	2025-11-05 10:08:30.04413
624	t	West Tripura	2025-11-05 10:08:30.04501	\N	\N	West Tripura	799001	\N	50.00	Tripura	2025-11-05 10:08:30.045013
625	t	Agra	2025-11-05 10:08:30.045862	\N	\N	Agra	282001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.045865
626	t	Aligarh	2025-11-05 10:08:30.046702	\N	\N	Aligarh	202001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.046704
627	t	Ambedkar Nagar	2025-11-05 10:08:30.047669	\N	\N	Ambedkar Nagar	224122	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.047671
628	t	Amethi	2025-11-05 10:08:30.048558	\N	\N	Amethi	227405	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.048561
629	t	Amroha	2025-11-05 10:08:30.049455	\N	\N	Amroha	244221	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.049458
630	t	Auraiya	2025-11-05 10:08:30.050313	\N	\N	Auraiya	206122	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.050315
631	t	Ayodhya	2025-11-05 10:08:30.051179	\N	\N	Ayodhya	224123	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.051182
632	t	Azamgarh	2025-11-05 10:08:30.051991	\N	\N	Azamgarh	276001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.051994
633	t	Baghpat	2025-11-05 10:08:30.052853	\N	\N	Baghpat	250609	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.052855
634	t	Bahraich	2025-11-05 10:08:30.053914	\N	\N	Bahraich	271801	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.053918
635	t	Ballia	2025-11-05 10:08:30.054927	\N	\N	Ballia	277001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.054931
636	t	Balrampur	2025-11-05 10:08:30.055831	\N	\N	Balrampur	271201	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.055836
637	t	Banda	2025-11-05 10:08:30.056812	\N	\N	Banda	210001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.056815
638	t	Barabanki	2025-11-05 10:08:30.057738	\N	\N	Barabanki	225001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.057742
639	t	Bareilly	2025-11-05 10:08:30.058853	\N	\N	Bareilly	243001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.058856
640	t	Basti	2025-11-05 10:08:30.060059	\N	\N	Basti	272001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.060062
641	t	Bhadohi	2025-11-05 10:08:30.061336	\N	\N	Bhadohi	221401	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.06134
642	t	Bijnor	2025-11-05 10:08:30.062518	\N	\N	Bijnor	246701	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.062522
643	t	Budaun	2025-11-05 10:08:30.063492	\N	\N	Budaun	243601	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.063495
644	t	Bulandshahr	2025-11-05 10:08:30.064494	\N	\N	Bulandshahr	203001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.064498
645	t	Chandauli	2025-11-05 10:08:30.065516	\N	\N	Chandauli	232104	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.06552
646	t	Chitrakoot	2025-11-05 10:08:30.066512	\N	\N	Chitrakoot	210204	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.066515
647	t	Deoria	2025-11-05 10:08:30.067509	\N	\N	Deoria	274001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.067512
648	t	Etah	2025-11-05 10:08:30.068453	\N	\N	Etah	207001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.068456
649	t	Etawah	2025-11-05 10:08:30.069309	\N	\N	Etawah	206001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.069312
650	t	Farrukhabad	2025-11-05 10:08:30.070162	\N	\N	Farrukhabad	209625	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.070165
651	t	Fatehpur	2025-11-05 10:08:30.071007	\N	\N	Fatehpur	212601	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.07101
652	t	Firozabad	2025-11-05 10:08:30.071915	\N	\N	Firozabad	283203	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.071919
653	t	Gautam Buddha Nagar	2025-11-05 10:08:30.072802	\N	\N	Gautam Buddha Nagar	201301	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.072806
654	t	Ghaziabad	2025-11-05 10:08:30.073666	\N	\N	Ghaziabad	201001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.073669
655	t	Ghazipur	2025-11-05 10:08:30.074495	\N	\N	Ghazipur	233001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.074498
656	t	Gonda	2025-11-05 10:08:30.075307	\N	\N	Gonda	271001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.07531
657	t	Gorakhpur	2025-11-05 10:08:30.076119	\N	\N	Gorakhpur	273001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.076122
658	t	Hamirpur	2025-11-05 10:08:30.077008	\N	\N	Hamirpur	210301	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.077011
659	t	Hapur	2025-11-05 10:08:30.07784	\N	\N	Hapur	245101	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.077843
660	t	Hardoi	2025-11-05 10:08:30.078731	\N	\N	Hardoi	241001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.078734
661	t	Hathras	2025-11-05 10:08:30.079717	\N	\N	Hathras	204101	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.07972
662	t	Jalaun	2025-11-05 10:08:30.080688	\N	\N	Jalaun	285123	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.080691
663	t	Jaunpur	2025-11-05 10:08:30.081617	\N	\N	Jaunpur	222001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.08162
664	t	Jhansi	2025-11-05 10:08:30.082441	\N	\N	Jhansi	284001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.082444
665	t	Kannauj	2025-11-05 10:08:30.083417	\N	\N	Kannauj	209727	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.08342
666	t	Kanpur Dehat	2025-11-05 10:08:30.084313	\N	\N	Kanpur Dehat	209111	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.084316
667	t	Kanpur Nagar	2025-11-05 10:08:30.085171	\N	\N	Kanpur Nagar	208001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.085174
668	t	Kasganj	2025-11-05 10:08:30.086009	\N	\N	Kasganj	207123	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.086012
669	t	Kaushambi	2025-11-05 10:08:30.086874	\N	\N	Kaushambi	212201	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.086877
670	t	Kushinagar	2025-11-05 10:08:30.087728	\N	\N	Kushinagar	274403	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.087731
671	t	Lakhimpur Kheri	2025-11-05 10:08:30.088588	\N	\N	Lakhimpur Kheri	262701	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.088591
672	t	Lalitpur	2025-11-05 10:08:30.089468	\N	\N	Lalitpur	284403	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.08947
673	t	Lucknow	2025-11-05 10:08:30.090306	\N	\N	Lucknow	226001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.090309
674	t	Maharajganj	2025-11-05 10:08:30.091258	\N	\N	Maharajganj	273303	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.091261
675	t	Mahoba	2025-11-05 10:08:30.092056	\N	\N	Mahoba	210427	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.092059
676	t	Mainpuri	2025-11-05 10:08:30.092862	\N	\N	Mainpuri	205001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.092866
677	t	Mathura	2025-11-05 10:08:30.093708	\N	\N	Mathura	281001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.093711
678	t	Mau	2025-11-05 10:08:30.094546	\N	\N	Mau	275101	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.094549
679	t	Meerut	2025-11-05 10:08:30.095483	\N	\N	Meerut	250001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.095485
680	t	Mirzapur	2025-11-05 10:08:30.096359	\N	\N	Mirzapur	231001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.096362
681	t	Moradabad	2025-11-05 10:08:30.097312	\N	\N	Moradabad	244001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.097314
682	t	Muzaffarnagar	2025-11-05 10:08:30.098161	\N	\N	Muzaffarnagar	251001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.098163
683	t	Pilibhit	2025-11-05 10:08:30.098959	\N	\N	Pilibhit	262001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.098962
684	t	Pratapgarh	2025-11-05 10:08:30.099757	\N	\N	Pratapgarh	230001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.09976
685	t	Prayagraj	2025-11-05 10:08:30.100576	\N	\N	Prayagraj	211001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.100579
686	t	Raebareli	2025-11-05 10:08:30.101465	\N	\N	Raebareli	229001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.101468
687	t	Rampur	2025-11-05 10:08:30.102275	\N	\N	Rampur	244901	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.102278
688	t	Saharanpur	2025-11-05 10:08:30.103128	\N	\N	Saharanpur	247001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.103131
689	t	Sambhal	2025-11-05 10:08:30.10394	\N	\N	Sambhal	244302	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.103943
690	t	Sant Kabir Nagar	2025-11-05 10:08:30.10478	\N	\N	Sant Kabir Nagar	272175	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.104783
691	t	Shahjahanpur	2025-11-05 10:08:30.105597	\N	\N	Shahjahanpur	242001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.1056
692	t	Shamli	2025-11-05 10:08:30.106394	\N	\N	Shamli	247776	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.106396
693	t	Shravasti	2025-11-05 10:08:30.107161	\N	\N	Shravasti	271831	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.107164
694	t	Siddharthnagar	2025-11-05 10:08:30.107922	\N	\N	Siddharthnagar	272207	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.107925
695	t	Sitapur	2025-11-05 10:08:30.108789	\N	\N	Sitapur	261001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.108792
696	t	Sonbhadra	2025-11-05 10:08:30.109603	\N	\N	Sonbhadra	231216	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.109606
697	t	Sultanpur	2025-11-05 10:08:30.110404	\N	\N	Sultanpur	228001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.110407
698	t	Unnao	2025-11-05 10:08:30.111154	\N	\N	Unnao	209801	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.111157
699	t	Varanasi	2025-11-05 10:08:30.111934	\N	\N	Varanasi	221001	\N	50.00	Uttar Pradesh	2025-11-05 10:08:30.111938
700	t	Almora	2025-11-05 10:08:30.112741	\N	\N	Almora	263601	\N	50.00	Uttarakhand	2025-11-05 10:08:30.112744
701	t	Bageshwar	2025-11-05 10:08:30.113533	\N	\N	Bageshwar	263642	\N	50.00	Uttarakhand	2025-11-05 10:08:30.113536
702	t	Chamoli	2025-11-05 10:08:30.114362	\N	\N	Chamoli	246401	\N	50.00	Uttarakhand	2025-11-05 10:08:30.114365
703	t	Champawat	2025-11-05 10:08:30.115172	\N	\N	Champawat	262523	\N	50.00	Uttarakhand	2025-11-05 10:08:30.115175
704	t	Dehradun	2025-11-05 10:08:30.116035	\N	\N	Dehradun	248001	\N	50.00	Uttarakhand	2025-11-05 10:08:30.116038
705	t	Haridwar	2025-11-05 10:08:30.116815	\N	\N	Haridwar	249401	\N	50.00	Uttarakhand	2025-11-05 10:08:30.116818
706	t	Nainital	2025-11-05 10:08:30.117688	\N	\N	Nainital	263001	\N	50.00	Uttarakhand	2025-11-05 10:08:30.11769
707	t	Pauri Garhwal	2025-11-05 10:08:30.118629	\N	\N	Pauri Garhwal	246001	\N	50.00	Uttarakhand	2025-11-05 10:08:30.118632
708	t	Pithoragarh	2025-11-05 10:08:30.119444	\N	\N	Pithoragarh	262501	\N	50.00	Uttarakhand	2025-11-05 10:08:30.119446
709	t	Rudraprayag	2025-11-05 10:08:30.12024	\N	\N	Rudraprayag	246171	\N	50.00	Uttarakhand	2025-11-05 10:08:30.120243
710	t	Tehri Garhwal	2025-11-05 10:08:30.121034	\N	\N	Tehri Garhwal	249145	\N	50.00	Uttarakhand	2025-11-05 10:08:30.121036
711	t	Udham Singh Nagar	2025-11-05 10:08:30.121842	\N	\N	Udham Singh Nagar	263153	\N	50.00	Uttarakhand	2025-11-05 10:08:30.121845
712	t	Uttarkashi	2025-11-05 10:08:30.122647	\N	\N	Uttarkashi	249193	\N	50.00	Uttarakhand	2025-11-05 10:08:30.12265
713	t	Alipurduar	2025-11-05 10:08:30.123479	\N	\N	Alipurduar	736121	\N	50.00	West Bengal	2025-11-05 10:08:30.123488
714	t	Bankura	2025-11-05 10:08:30.124282	\N	\N	Bankura	722101	\N	50.00	West Bengal	2025-11-05 10:08:30.124285
715	t	Birbhum	2025-11-05 10:08:30.125084	\N	\N	Birbhum	731101	\N	50.00	West Bengal	2025-11-05 10:08:30.125087
716	t	Cooch Behar	2025-11-05 10:08:30.125898	\N	\N	Cooch Behar	736101	\N	50.00	West Bengal	2025-11-05 10:08:30.125901
717	t	Dakshin Dinajpur	2025-11-05 10:08:30.126693	\N	\N	Dakshin Dinajpur	733101	\N	50.00	West Bengal	2025-11-05 10:08:30.126695
718	t	Darjeeling	2025-11-05 10:08:30.127488	\N	\N	Darjeeling	734101	\N	50.00	West Bengal	2025-11-05 10:08:30.12749
719	t	Hooghly	2025-11-05 10:08:30.12831	\N	\N	Hooghly	712101	\N	50.00	West Bengal	2025-11-05 10:08:30.128331
720	t	Howrah	2025-11-05 10:08:30.129173	\N	\N	Howrah	711101	\N	50.00	West Bengal	2025-11-05 10:08:30.129176
721	t	Jalpaiguri	2025-11-05 10:08:30.129954	\N	\N	Jalpaiguri	735101	\N	50.00	West Bengal	2025-11-05 10:08:30.129964
722	t	Jhargram	2025-11-05 10:08:30.130763	\N	\N	Jhargram	721507	\N	50.00	West Bengal	2025-11-05 10:08:30.130766
723	t	Kalimpong	2025-11-05 10:08:30.131555	\N	\N	Kalimpong	734301	\N	50.00	West Bengal	2025-11-05 10:08:30.131558
724	t	Kolkata	2025-11-05 10:08:30.132357	\N	\N	Kolkata	700001	\N	50.00	West Bengal	2025-11-05 10:08:30.13236
725	t	Malda	2025-11-05 10:08:30.13318	\N	\N	Malda	732101	\N	50.00	West Bengal	2025-11-05 10:08:30.133182
726	t	Murshidabad	2025-11-05 10:08:30.133964	\N	\N	Murshidabad	742101	\N	50.00	West Bengal	2025-11-05 10:08:30.133966
727	t	Nadia	2025-11-05 10:08:30.134823	\N	\N	Nadia	741101	\N	50.00	West Bengal	2025-11-05 10:08:30.134825
728	t	North 24 Parganas	2025-11-05 10:08:30.13564	\N	\N	North 24 Parganas	700110	\N	50.00	West Bengal	2025-11-05 10:08:30.135643
729	t	Paschim Bardhaman	2025-11-05 10:08:30.136435	\N	\N	Paschim Bardhaman	713201	\N	50.00	West Bengal	2025-11-05 10:08:30.136438
730	t	Paschim Medinipur	2025-11-05 10:08:30.137364	\N	\N	Paschim Medinipur	721101	\N	50.00	West Bengal	2025-11-05 10:08:30.137366
731	t	Purba Bardhaman	2025-11-05 10:08:30.138148	\N	\N	Purba Bardhaman	713101	\N	50.00	West Bengal	2025-11-05 10:08:30.138151
732	t	Purba Medinipur	2025-11-05 10:08:30.138947	\N	\N	Purba Medinipur	721401	\N	50.00	West Bengal	2025-11-05 10:08:30.13895
733	t	Purulia	2025-11-05 10:08:30.13972	\N	\N	Purulia	723101	\N	50.00	West Bengal	2025-11-05 10:08:30.139723
734	t	South 24 Parganas	2025-11-05 10:08:30.140549	\N	\N	South 24 Parganas	700140	\N	50.00	West Bengal	2025-11-05 10:08:30.140551
735	t	Uttar Dinajpur	2025-11-05 10:08:30.141495	\N	\N	Uttar Dinajpur	733134	\N	50.00	West Bengal	2025-11-05 10:08:30.141498
736	t	Nicobar	2025-11-05 10:08:30.142346	\N	\N	Nicobar	744301	\N	50.00	Andaman and Nicobar Islands	2025-11-05 10:08:30.142348
737	t	North and Middle Andaman	2025-11-05 10:08:30.143122	\N	\N	North and Middle Andaman	744205	\N	50.00	Andaman and Nicobar Islands	2025-11-05 10:08:30.143125
738	t	South Andaman	2025-11-05 10:08:30.143917	\N	\N	South Andaman	744101	\N	50.00	Andaman and Nicobar Islands	2025-11-05 10:08:30.14392
739	t	Chandigarh	2025-11-05 10:08:30.144857	\N	\N	Chandigarh	160017	\N	50.00	Chandigarh	2025-11-05 10:08:30.14486
740	t	Dadra and Nagar Haveli	2025-11-05 10:08:30.145719	\N	\N	Dadra and Nagar Haveli	396230	\N	50.00	Dadra and Nagar Haveli and Daman and Diu	2025-11-05 10:08:30.145721
741	t	Daman	2025-11-05 10:08:30.146625	\N	\N	Daman	396210	\N	50.00	Dadra and Nagar Haveli and Daman and Diu	2025-11-05 10:08:30.146628
742	t	Diu	2025-11-05 10:08:30.147442	\N	\N	Diu	362520	\N	50.00	Dadra and Nagar Haveli and Daman and Diu	2025-11-05 10:08:30.147445
743	t	Lakshadweep	2025-11-05 10:08:30.148248	\N	\N	Lakshadweep	682555	\N	50.00	Lakshadweep	2025-11-05 10:08:30.14825
744	t	Central Delhi	2025-11-05 10:08:30.149033	\N	\N	Central Delhi	110001	\N	50.00	Delhi	2025-11-05 10:08:30.149036
745	t	East Delhi	2025-11-05 10:08:30.149838	\N	\N	East Delhi	110096	\N	50.00	Delhi	2025-11-05 10:08:30.149841
746	t	New Delhi	2025-11-05 10:08:30.150681	\N	\N	New Delhi	110011	\N	50.00	Delhi	2025-11-05 10:08:30.150683
747	t	North Delhi	2025-11-05 10:08:30.151547	\N	\N	North Delhi	110007	\N	50.00	Delhi	2025-11-05 10:08:30.15155
748	t	North East Delhi	2025-11-05 10:08:30.152372	\N	\N	North East Delhi	110053	\N	50.00	Delhi	2025-11-05 10:08:30.152375
749	t	North West Delhi	2025-11-05 10:08:30.153184	\N	\N	North West Delhi	110081	\N	50.00	Delhi	2025-11-05 10:08:30.153187
750	t	Shahdara	2025-11-05 10:08:30.154057	\N	\N	Shahdara	110032	\N	50.00	Delhi	2025-11-05 10:08:30.154061
751	t	South Delhi	2025-11-05 10:08:30.155014	\N	\N	South Delhi	110025	\N	50.00	Delhi	2025-11-05 10:08:30.155016
752	t	South East Delhi	2025-11-05 10:08:30.15584	\N	\N	South East Delhi	110044	\N	50.00	Delhi	2025-11-05 10:08:30.155843
753	t	South West Delhi	2025-11-05 10:08:30.156703	\N	\N	South West Delhi	110061	\N	50.00	Delhi	2025-11-05 10:08:30.156705
754	t	West Delhi	2025-11-05 10:08:30.157555	\N	\N	West Delhi	110026	\N	50.00	Delhi	2025-11-05 10:08:30.157557
755	t	Kargil	2025-11-05 10:08:30.158392	\N	\N	Kargil	194103	\N	50.00	Ladakh	2025-11-05 10:08:30.158395
756	t	Leh	2025-11-05 10:08:30.159204	\N	\N	Leh	194101	\N	50.00	Ladakh	2025-11-05 10:08:30.159207
757	t	Anantnag	2025-11-05 10:08:30.160039	\N	\N	Anantnag	192101	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.160042
758	t	Bandipora	2025-11-05 10:08:30.16085	\N	\N	Bandipora	193502	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.160853
759	t	Baramulla	2025-11-05 10:08:30.161722	\N	\N	Baramulla	193101	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.161725
760	t	Budgam	2025-11-05 10:08:30.162595	\N	\N	Budgam	191111	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.162597
761	t	Doda	2025-11-05 10:08:30.163421	\N	\N	Doda	182202	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.163424
762	t	Ganderbal	2025-11-05 10:08:30.164255	\N	\N	Ganderbal	191201	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.164258
763	t	Jammu	2025-11-05 10:08:30.1652	\N	\N	Jammu	180001	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.165202
764	t	Kathua	2025-11-05 10:08:30.166095	\N	\N	Kathua	184101	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.166098
765	t	Kishtwar	2025-11-05 10:08:30.166906	\N	\N	Kishtwar	182204	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.166908
766	t	Kulgam	2025-11-05 10:08:30.167823	\N	\N	Kulgam	192231	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.167826
767	t	Kupwara	2025-11-05 10:08:30.168826	\N	\N	Kupwara	193222	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.168829
768	t	Poonch	2025-11-05 10:08:30.169844	\N	\N	Poonch	185101	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.169849
769	t	Pulwama	2025-11-05 10:08:30.171049	\N	\N	Pulwama	192301	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.171052
770	t	Rajouri	2025-11-05 10:08:30.172072	\N	\N	Rajouri	185131	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.172074
771	t	Ramban	2025-11-05 10:08:30.173046	\N	\N	Ramban	182144	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.173049
772	t	Reasi	2025-11-05 10:08:30.17419	\N	\N	Reasi	182311	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.174193
773	t	Samba	2025-11-05 10:08:30.175117	\N	\N	Samba	184121	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.17512
774	t	Shopian	2025-11-05 10:08:30.176004	\N	\N	Shopian	192303	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.176006
775	t	Srinagar	2025-11-05 10:08:30.177115	\N	\N	Srinagar	190001	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.177118
776	t	Udhampur	2025-11-05 10:08:30.178029	\N	\N	Udhampur	182101	\N	50.00	Jammu and Kashmir	2025-11-05 10:08:30.178032
777	t	Anupgarh	2025-11-05 10:08:30.179011	\N	\N	Anupgarh	335701	\N	50.00	Rajasthan	2025-11-05 10:08:30.179014
778	t	Balotra	2025-11-05 10:08:30.179803	\N	\N	Balotra	344022	\N	50.00	Rajasthan	2025-11-05 10:08:30.179805
779	t	Beawar	2025-11-05 10:08:30.180607	\N	\N	Beawar	305901	\N	50.00	Rajasthan	2025-11-05 10:08:30.18061
780	t	Deeg	2025-11-05 10:08:30.181434	\N	\N	Deeg	321203	\N	50.00	Rajasthan	2025-11-05 10:08:30.181436
781	t	Didwana-Kuchaman	2025-11-05 10:08:30.182241	\N	\N	Didwana-Kuchaman	341508	\N	50.00	Rajasthan	2025-11-05 10:08:30.182244
782	t	Dudu	2025-11-05 10:08:30.183079	\N	\N	Dudu	303008	\N	50.00	Rajasthan	2025-11-05 10:08:30.183082
783	t	Kekri	2025-11-05 10:08:30.183906	\N	\N	Kekri	305404	\N	50.00	Rajasthan	2025-11-05 10:08:30.183908
784	t	Khairthal-Tijara	2025-11-05 10:08:30.18476	\N	\N	Khairthal-Tijara	301404	\N	50.00	Rajasthan	2025-11-05 10:08:30.184763
785	t	Kotputli-Behror	2025-11-05 10:08:30.185562	\N	\N	Kotputli-Behror	303108	\N	50.00	Rajasthan	2025-11-05 10:08:30.185565
786	t	Phalodi	2025-11-05 10:08:30.186385	\N	\N	Phalodi	342301	\N	50.00	Rajasthan	2025-11-05 10:08:30.186389
787	t	Salumbar	2025-11-05 10:08:30.187181	\N	\N	Salumbar	313027	\N	50.00	Rajasthan	2025-11-05 10:08:30.187184
\.


--
-- Data for Name: franchise_requests; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.franchise_requests (id, aadhar_number, admin_comments, approved_at, business_address, business_name, business_registration_number, contact_email, contact_phone, created_at, district_id, district_name, document_ids, gst_number, pan_number, reviewed_at, reviewed_by, state, status, updated_at, years_of_experience, user_id) FROM stdin;
1	123456789012	undefined	\N	indore 	arpita	REG213234	arpita@gmail.com	8889084453	2025-11-11 08:36:27.038432	332	Indore	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/franchisee/documents/22_arpita/1/EAadhaar_0515451080946120210716160557_19082025104548.pdf,https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/franchisee/documents/22_arpita/1/1111.jpg	GST123456789	ABNH8787F	2025-11-11 08:37:08.59645	24	Madhya Pradesh	APPROVED	2025-11-11 08:37:08.602144	1	22
2	753048444952	\N	\N	House no 401 , Bhagyashree Colony, Vijay Nagar, Indore	Abhishek Kachhawa	RANPU2025111110382110	abhishekh@acoreithub.com	+91 9644782290	2025-11-12 11:53:35.827734	332	Indore	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/franchisee/documents/26_abhishek-kachhawa/2/doremon.png,https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/franchisee/documents/26_abhishek-kachhawa/2/doremon1.png	27ABCDE1234F2Z5	LBZPK8131R	\N	\N	Madhya Pradesh	PENDING	2025-11-12 11:53:37.590831	2	26
3	753048444952	undefined	\N	15 Ward pala, Tarana, Ujjain	Abhishek Kachhawa	RANPU202511111382110	abhishekkachhawa1205@gmail.com	+919644782290	2025-11-13 06:15:53.033858	360	Ujjain	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/franchisee/documents/26_abhishek-kachhawa/3/doremon3.png,https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/franchisee/documents/26_abhishek-kachhawa/3/doremon2.png	AAAPL1234C1Z1	LBZPK8131R	2025-11-13 06:49:38.518051	24	Madhya Pradesh	APPROVED	2025-11-13 06:49:38.519962	2	26
4	753048444952	\N	\N	15 Ward pala, Tarana, Ujjain	Abhishek Kachhawa	RANPU202511111806314611	abhishekkachhawa1205@gmail.com	9644782290	2025-11-18 06:39:27.096357	333	Jabalpur	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/franchisee/documents/26_abhishek-kachhawa/4/draze_image.png,https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/franchisee/documents/26_abhishek-kachhawa/4/doremon1.png	AAAPL1234C1Z1	PWVPK0831F	\N	\N	Madhya Pradesh	PENDING	2025-11-18 06:39:28.952917	4	26
5	548976494664	\N	\N	abhi hshsj hsd.	Abhishek Kachhawa	RANPU2025111110382110	abhishekh@acoreithub.com	6497888863	2025-11-18 09:33:50.694364	325	Dewas	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/franchisee/documents/26_abhishek-kachhawa/5/1000249354.jpg,https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/franchisee/documents/26_abhishek-kachhawa/5/1000249352.jpg	07ABCDE1234F2Z5	LBZPK8131R	\N	\N	Madhya Pradesh	PENDING	2025-11-18 09:33:52.114285	2	26
6	753048444952	\N	\N	abhishek	Dummy User	DUMMY-1762844256607	dummy@acoreithub.com	9012345678	2025-11-25 13:42:39.002866	336	Khandwa	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/franchisee/documents/23_dummy-user/6/1940.jpg,https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/franchisee/documents/23_dummy-user/6/2062.jpg	27ABCDE1234F1Z5	LBZPK8131R	\N	\N	Madhya Pradesh	PENDING	2025-11-25 13:42:40.611802	2	23
\.


--
-- Data for Name: franchisee_bank_details; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.franchisee_bank_details (id, account_name, account_number, account_type, bank_name, branch_name, created_at, ifsc_code, is_primary, is_verified, updated_at, upi_id, verified_at, verified_by, user_id) FROM stdin;
\.


--
-- Data for Name: franchisee_districts; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.franchisee_districts (id, active, available_balance, contact_email, contact_phone, created_at, district_id, district_name, end_date, office_address, revenue_share_percentage, start_date, state, status, total_commission, total_properties, total_revenue, total_transactions, updated_at, withdrawal_history, franchise_request_id, user_id) FROM stdin;
2	t	0.00	abhishekkachhawa1205@gmail.com	+919644782290	2025-11-13 06:49:38.518659	360	Ujjain	2026-11-13 06:49:38.518449	15 Ward pala, Tarana, Ujjain	50.00	2025-11-13 06:49:38.518461	Madhya Pradesh	ACTIVE	0.00	0	0.00	0	2025-11-13 11:01:51.630957	[]	3	26
1	t	\N	arpita@gmail.com	8889084453	2025-11-11 08:37:08.59863	332	Indore	2026-11-11 08:37:08.598075	indore 	50.00	2025-11-11 08:37:08.598112	Madhya Pradesh	ACTIVE	0.00	0	0.00	19	2025-12-04 05:43:26.999715	[]	1	22
\.


--
-- Data for Name: franchisee_withdrawal_requests; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.franchisee_withdrawal_requests (id, account_number, admin_comments, bank_name, created_at, ifsc_code, mobile_number, original_balance, payment_date, payment_reference, processed_at, processed_by, reason, requested_amount, screenshot_url, status, transaction_id, transaction_type, updated_at, updated_balance, bank_detail_id, franchisee_district_id) FROM stdin;
\.


--
-- Data for Name: message_reports; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.message_reports (id, admin_note, created_at, description, processed_at, reason, status, message_id, processed_by_id, reporter_id) FROM stdin;
\.


--
-- Data for Name: monthly_revenue_reports; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.monthly_revenue_reports (id, account_name, account_number, admin_comments, admin_share, bank_name, business_name, current_balance, district_id, district_name, emergency_withdrawals_amount, emergency_withdrawals_count, final_payable_amount, franchisee_commission, franchisee_name, generated_at, ifsc_code, month, new_subscriptions, payment_date, payment_due_date, payment_method, payment_reference, previous_balance, processed_at, processed_by, renewed_subscriptions, report_status, state, total_revenue, total_subscriptions, updated_at, year, bank_detail_id, franchisee_id, franchisee_district_id) FROM stdin;
1	\N	\N	\N	0.00	\N	arpita's Franchise	0.00	332	Indore	0.00	0	0.00	0.00	arpita	2025-12-01 01:00:00.757164	\N	11	22	\N	2025-12-16	\N	\N	0.00	\N	\N	0	PENDING	Madhya Pradesh	0.00	22	2025-12-01 01:00:00.75717	2025	\N	22	1
\.


--
-- Data for Name: otps; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.otps (id, attempts, blocked, code, created_at, email, expires_at, identifier, mobile_number, type, verified, verified_at) FROM stdin;
219	0	f	414259	2025-12-08 03:13:28.940303	\N	2025-12-08 03:23:28.940049	8839197575	8839197575	MOBILE	t	2025-12-08 03:14:05.108025
220	0	f	646792	2025-12-08 03:17:25.851545	\N	2025-12-08 03:27:25.851312	+919171617595	+919171617595	MOBILE	t	\N
221	0	f	898943	2025-12-08 03:17:36.30702	\N	2025-12-08 03:27:36.306803	+919171617595	+919171617595	MOBILE	t	2025-12-08 03:17:47.005624
223	0	f	403676	2025-12-08 03:28:40.895542	srk.acore13@gmail.com	2025-12-08 03:38:40.895229	srk.acore13@gmail.com	EMAIL_srk.acore13@gmail.com	EMAIL	f	\N
222	0	f	490322	2025-12-08 03:28:40.892612	\N	2025-12-08 03:38:40.892429	9171617595	9171617595	MOBILE	t	2025-12-08 03:29:08.42757
224	0	f	445810	2025-12-08 05:31:39.178926	\N	2025-12-08 05:41:39.178655	8839197575	8839197575	MOBILE	t	2025-12-08 05:32:20.264174
225	0	f	385450	2025-12-08 05:56:27.461808	\N	2025-12-08 06:06:27.461406	8839197575	8839197575	MOBILE	t	2025-12-08 05:56:38.527767
226	0	f	581253	2025-12-08 05:56:55.389001	\N	2025-12-08 06:06:55.388743	9171617595	9171617595	MOBILE	t	2025-12-08 05:57:50.492707
227	0	f	733738	2025-12-08 06:09:36.368925	\N	2025-12-08 06:19:36.367958	+917049433520	+917049433520	MOBILE	t	2025-12-08 06:10:29.75999
228	0	f	741533	2025-12-08 06:13:05.782903	\N	2025-12-08 06:23:05.782762	+919752810137	+919752810137	MOBILE	t	2025-12-08 06:13:38.747493
229	0	f	134573	2025-12-08 06:13:49.93572	\N	2025-12-08 06:23:49.934535	+917049433520	+917049433520	MOBILE	t	2025-12-08 06:14:06.398482
230	0	f	699653	2025-12-08 06:15:05.477869	\N	2025-12-08 06:25:05.477652	+919752810137	+919752810137	MOBILE	t	2025-12-08 06:15:31.613969
\.


--
-- Data for Name: payment_transactions; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.payment_transactions (id, amount, cancelled_at, completed_at, coupon_code, created_at, currency, discount_details, failure_code, failure_message, gateway_order_id, gateway_response, gateway_transaction_id, ip_address, original_amount, payment_date, payment_id, payment_method, payment_type, property_id, receipt_url, reference_id, refund_amount, refund_reason, refund_reference_id, refund_status, status, subscription_id, updated_at, user_agent, user_id) FROM stdin;
1	0.00	\N	\N	\N	2025-11-11 08:27:05.410347	INR	\N	TIMEOUT	Payment timed out	order_62e85708095944c2b5b36715edf9186a	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251111082705df1ef6ed	\N	\N	\N	\N	CANCELLED	1	2025-11-11 10:00:00.003013	\N	22
2	0.00	\N	\N	\N	2025-11-11 08:27:14.130705	INR	\N	TIMEOUT	Payment timed out	order_7b5f19e1fbf14dc093f9c9bc0b970d7e	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025111108271482ba523b	\N	\N	\N	\N	CANCELLED	1	2025-11-11 10:00:00.003166	\N	22
3	0.00	\N	\N	\N	2025-11-11 08:27:38.012791	INR	\N	TIMEOUT	Payment timed out	order_667dde00fa5845ea953cc928ad65e936	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025111108273881da0d80	\N	\N	\N	\N	CANCELLED	1	2025-11-11 10:00:00.003193	\N	22
4	0.00	\N	\N	\N	2025-11-11 08:28:16.373009	INR	\N	TIMEOUT	Payment timed out	order_2aec1e677cb443e2968ca6eb736f82fd	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251111082816c4f45376	\N	\N	\N	\N	CANCELLED	1	2025-11-11 10:00:00.003213	\N	22
5	0.00	\N	\N	\N	2025-11-11 08:29:19.67413	INR	\N	TIMEOUT	Payment timed out	order_a6e23015989a46b29da78988f239d085	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025111108291964eac616	\N	\N	\N	\N	CANCELLED	1	2025-11-11 10:00:00.003231	\N	22
6	0.00	\N	\N	\N	2025-11-11 08:32:20.590034	INR	\N	TIMEOUT	Payment timed out	order_4f1afec2a35841c49b25e4f40cd7d8f9	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251111083220d81375a7	\N	\N	\N	\N	CANCELLED	1	2025-11-11 10:00:00.003252	\N	22
7	0.00	\N	\N	\N	2025-11-11 08:37:11.77946	INR	\N	TIMEOUT	Payment timed out	order_3951a05b6adf4ccea7c48eae5e67f16f	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025111108371142fc2fb6	\N	\N	\N	\N	CANCELLED	1	2025-11-11 10:00:00.003272	\N	22
8	0.00	\N	\N	\N	2025-11-11 08:37:27.114151	INR	\N	TIMEOUT	Payment timed out	order_9e1a2477e96842d4bf8d04ac9a55fc8f	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251111083727415307d3	\N	\N	\N	\N	CANCELLED	1	2025-11-11 10:00:00.003319	\N	2
9	0.00	\N	\N	\N	2025-11-11 09:07:00.968724	INR	\N	TIMEOUT	Payment timed out	order_479b769dee24423cb8d911f4e2f87fde	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251111090700209636b8	\N	\N	\N	\N	CANCELLED	1	2025-11-11 11:00:00.00439	\N	25
10	0.00	\N	\N	\N	2025-11-11 09:22:42.848918	INR	\N	TIMEOUT	Payment timed out	order_bfcb51d7b21249e7b0700f51ca7fbf67	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251111092242fad58b3a	\N	\N	\N	\N	CANCELLED	1	2025-11-11 11:00:00.004601	\N	13
11	0.00	\N	\N	\N	2025-11-11 09:51:38.766784	INR	\N	TIMEOUT	Payment timed out	order_36b41df3c3d046e895246a6d3d44ae24	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511110951380d0e259c	\N	\N	\N	\N	CANCELLED	1	2025-11-11 11:00:00.004758	\N	13
12	0.00	\N	\N	\N	2025-11-11 10:13:38.316627	INR	\N	TIMEOUT	Payment timed out	order_41cbc8e6b9284144a430dc0ed358c79e	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511111013382b74fde7	\N	\N	\N	\N	CANCELLED	1	2025-11-11 12:00:00.007055	\N	13
13	0.00	\N	\N	\N	2025-11-11 10:25:16.447158	INR	\N	TIMEOUT	Payment timed out	order_717191bf76c2448c9314a60b1186c5ff	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025111110251661a891fb	\N	\N	\N	\N	CANCELLED	1	2025-11-11 12:00:00.007193	\N	13
14	0.00	\N	\N	\N	2025-11-11 10:28:45.440836	INR	\N	TIMEOUT	Payment timed out	order_9d7a83ca068e411dbff5e2fb24ec3de1	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251111102845cdb5f002	\N	\N	\N	\N	CANCELLED	1	2025-11-11 12:00:00.00722	\N	13
15	0.00	\N	\N	\N	2025-11-11 10:40:34.958272	INR	\N	TIMEOUT	Payment timed out	order_2abae66cbee6426dbc774962a68300c0	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251111104034e2f3bca0	\N	\N	\N	\N	CANCELLED	1	2025-11-11 12:00:00.00724	\N	26
16	0.00	\N	\N		2025-11-14 09:12:21.526787	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_8bfd8ca6fe7c4db882f2e74b57cc8549	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025111409122153a2efbf	\N	\N	\N	\N	CANCELLED	1	2025-11-14 11:00:00.0037	\N	26
17	0.00	\N	\N		2025-11-14 09:12:31.30021	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_d150f93ffeef438a998ecc78fc3efcc7	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251114091231b5cf725b	\N	\N	\N	\N	CANCELLED	1	2025-11-14 11:00:00.003967	\N	26
18	0.00	\N	\N		2025-11-14 09:15:25.480342	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_a66fd1eef2634f4a8c144f7d4b04f3d1	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251114091525d10f44c4	\N	\N	\N	\N	CANCELLED	1	2025-11-14 11:00:00.004	\N	26
19	0.00	\N	\N		2025-11-14 09:22:28.947051	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_cfcae77560cf4e29a345fd1a119cb4d2	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511140922285a791dd0	\N	\N	\N	\N	CANCELLED	1	2025-11-14 11:00:00.004029	\N	26
20	116.82	\N	\N	\N	2025-11-14 09:59:24.578396	INR	\N	TIMEOUT	Payment timed out	order_c2857e7b4a5f442881afcf9befd19c15	\N	\N	\N	116.82	\N	\N	\N	REEL_PURCHASE	\N	\N	RANPP20251114095924d5804fda	\N	\N	\N	\N	CANCELLED	\N	2025-11-14 11:00:00.004052	\N	26
21	116.82	\N	\N	\N	2025-11-14 10:12:46.63626	INR	\N	TIMEOUT	Payment timed out	order_7a493d9940844f58acd138f98766f97e	\N	\N	\N	116.82	\N	\N	\N	REEL_PURCHASE	\N	\N	RANPP20251114101246db98033a	\N	\N	\N	\N	CANCELLED	\N	2025-11-14 12:00:00.005545	\N	26
22	116.82	\N	\N	\N	2025-11-14 10:32:45.826734	INR	\N	TIMEOUT	Payment timed out	order_ea7b74a22985461f82c868ed1d72a0e9	\N	\N	\N	116.82	\N	\N	\N	REEL_PURCHASE	\N	\N	RANPP20251114103245a11d500f	\N	\N	\N	\N	CANCELLED	\N	2025-11-14 12:00:00.005787	\N	26
23	0.00	\N	\N		2025-11-14 10:35:36.481308	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_7f63cc02353f41dc9fa53bc3a2780893	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511141035365622dca4	\N	\N	\N	\N	CANCELLED	1	2025-11-14 12:00:00.005875	\N	26
24	0.00	\N	\N		2025-11-14 10:46:06.366871	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_c6af2dcb081549259cdeba8606e7f5ea	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511141046067e7567b9	\N	\N	\N	\N	CANCELLED	1	2025-11-14 12:00:00.005911	\N	26
25	0.00	\N	\N		2025-11-14 10:51:32.229487	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_410f6222731c43c99669125788a1fe51	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251114105132ae594466	\N	\N	\N	\N	CANCELLED	1	2025-11-14 12:00:00.005944	\N	26
26	0.00	\N	\N		2025-11-14 10:53:10.213193	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_fe3d35eb01aa4d93a32ff73828845d5b	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511141053103f55c022	\N	\N	\N	\N	CANCELLED	1	2025-11-14 12:00:00.005969	\N	26
27	0.00	\N	\N		2025-11-14 10:54:03.585053	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_2b047fe4199440d29d44410e763aa577	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251114105403318e4488	\N	\N	\N	\N	CANCELLED	1	2025-11-14 12:00:00.005994	\N	26
28	0.00	\N	\N		2025-11-14 10:59:28.733413	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_8f536218a00347d7a802f7ceba333ea3	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251114105928d6d014be	\N	\N	\N	\N	CANCELLED	1	2025-11-14 12:00:00.006022	\N	26
29	0.00	\N	\N		2025-11-14 11:51:55.168284	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_a9941e62f2b74c509da853aba7b41ae5	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511141151554f0e292d	\N	\N	\N	\N	CANCELLED	1	2025-11-14 13:00:00.0064	\N	26
30	0.00	\N	\N		2025-11-14 11:52:00.844278	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_158f755333f34285abba4a0be7e4bd6f	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025111411520050601185	\N	\N	\N	\N	CANCELLED	1	2025-11-14 13:00:00.006523	\N	26
31	0.00	\N	\N		2025-11-14 11:52:18.227382	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_4fb866d64fba4f6585ac7f0d138a6eed	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025111411521896c7a137	\N	\N	\N	\N	CANCELLED	1	2025-11-14 13:00:00.006558	\N	26
32	0.00	\N	\N		2025-11-14 11:54:20.099848	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_da3391496090423684248fcb96d6be7a	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025111411542018ee059a	\N	\N	\N	\N	CANCELLED	1	2025-11-14 13:00:00.006594	\N	26
33	116.82	\N	\N	\N	2025-11-14 11:55:45.157082	INR	\N	TIMEOUT	Payment timed out	order_0e97b299c42c452289831dd81da76219	\N	\N	\N	116.82	\N	\N	\N	REEL_PURCHASE	\N	\N	RANPP20251114115545de8c2cbd	\N	\N	\N	\N	CANCELLED	\N	2025-11-14 13:00:00.00662	\N	26
34	116.82	\N	\N	\N	2025-11-14 12:10:39.770869	INR	\N	TIMEOUT	Payment timed out	order_b3a27da8689c4624b2187146a1cf25e4	\N	\N	\N	116.82	\N	\N	\N	REEL_PURCHASE	\N	\N	RANPP202511141210398237d926	\N	\N	\N	\N	CANCELLED	\N	2025-11-14 14:00:00.006011	\N	26
35	116.82	\N	\N	\N	2025-11-14 12:19:17.652259	INR	\N	TIMEOUT	Payment timed out	order_5f8d5d7b026f4580873245b79c24243a	\N	\N	\N	116.82	\N	\N	\N	REEL_PURCHASE	\N	\N	RANPP2025111412191743d8c9f6	\N	\N	\N	\N	CANCELLED	\N	2025-11-14 14:00:00.006192	\N	26
36	99.00	\N	\N	\N	2025-11-14 12:32:18.815663	INR	\N	TIMEOUT	Payment timed out	order_3382d3bbc1f64648987f46a2d453a545	\N	\N	\N	99.00	\N	\N	\N	REEL_PURCHASE	\N	\N	RANPP2025111412321843edfe8c	\N	\N	\N	\N	CANCELLED	\N	2025-11-14 14:00:00.00626	\N	26
37	116.82	\N	\N	\N	2025-11-14 12:33:13.778928	INR	\N	TIMEOUT	Payment timed out	order_c1ea1d4d95bc4b09807b0e23bfdd9a6e	\N	\N	\N	116.82	\N	\N	\N	REEL_PURCHASE	\N	\N	RANPP20251114123313ea5e0f04	\N	\N	\N	\N	CANCELLED	\N	2025-11-14 14:00:00.006297	\N	26
38	0.00	\N	\N		2025-11-14 12:36:00.416004	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_d20b0c90e1f14b9d857c529ca38aee15	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511141236006f00fdff	\N	\N	\N	\N	CANCELLED	1	2025-11-14 14:00:00.006329	\N	26
39	0.00	\N	\N	\N	2025-11-15 08:29:45.61664	INR	\N	TIMEOUT	Payment timed out	order_5e53f765f4bc40eb8559514d194b69c0	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251115082945fff38616	\N	\N	\N	\N	CANCELLED	1	2025-11-15 10:00:00.003633	\N	23
40	0.00	\N	\N	\N	2025-11-15 08:49:46.999451	INR	\N	TIMEOUT	Payment timed out	order_1b15c6b3331f47b7910ec8480d0642c7	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025111508494643b378a0	\N	\N	\N	\N	CANCELLED	1	2025-11-15 10:00:00.003766	\N	28
41	0.00	\N	\N	\N	2025-11-15 09:47:50.110892	INR	\N	TIMEOUT	Payment timed out	order_5718ef7394b7406b9c8cc183fa81513b	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025111509475045598f4d	\N	\N	\N	\N	CANCELLED	1	2025-11-15 11:00:00.004367	\N	29
42	0.00	\N	\N	\N	2025-11-15 13:18:33.301388	INR	\N	TIMEOUT	Payment timed out	order_7ccd63656e4a402bad3110ff5c868068	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511151318330dc91400	\N	\N	\N	\N	CANCELLED	1	2025-11-15 15:00:00.007821	\N	30
43	0.00	\N	\N	\N	2025-11-15 13:33:41.428195	INR	\N	TIMEOUT	Payment timed out	order_7c0db4c2687a4b7bb8928f9f4cbe3dd5	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251115133341414e71f3	\N	\N	\N	\N	CANCELLED	1	2025-11-15 15:00:00.007951	\N	31
44	0.00	\N	\N	\N	2025-11-16 04:18:33.491244	INR	\N	TIMEOUT	Payment timed out	order_a615e4377e8d4281b206bf18fcff1f49	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251116041833e75132f9	\N	\N	\N	\N	CANCELLED	1	2025-11-16 06:00:00.003835	\N	32
45	0.00	\N	\N	\N	2025-11-16 04:29:24.958747	INR	\N	TIMEOUT	Payment timed out	order_4e08e4121d02461880ad8fecaf4e405f	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511160429240dbfbad5	\N	\N	\N	\N	CANCELLED	1	2025-11-16 06:00:00.003967	\N	33
46	0.00	\N	\N	\N	2025-11-16 04:35:12.481402	INR	\N	TIMEOUT	Payment timed out	order_b26a775b14f1491bb5d47772689eeeaf	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511160435129189e250	\N	\N	\N	\N	CANCELLED	1	2025-11-16 06:00:00.00399	\N	34
47	0.00	\N	\N	\N	2025-11-16 08:59:14.32296	INR	\N	TIMEOUT	Payment timed out	order_fb7b1675d0464f25a690156168cbf280	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251116085914dd662f19	\N	\N	\N	\N	CANCELLED	1	2025-11-16 10:00:00.006108	\N	35
48	0.00	\N	\N	\N	2025-11-17 07:07:08.185156	INR	\N	TIMEOUT	Payment timed out	order_f2efbe63857f4abf96a2c718db8ce7f9	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251117070708c92f26d2	\N	\N	\N	\N	CANCELLED	1	2025-11-17 09:00:00.003251	\N	36
49	0.00	\N	\N	\N	2025-11-17 08:32:44.117152	INR	\N	TIMEOUT	Payment timed out	order_3af3f8c887fc44ec9f68618fe65100bc	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251117083244e9c7c2dd	\N	\N	\N	\N	CANCELLED	1	2025-11-17 10:00:00.006984	\N	15
50	0.00	\N	\N		2025-11-17 10:44:19.842615	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_e13f349547534589b3ac186f0122ddad	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251117104419049db2d3	\N	\N	\N	\N	CANCELLED	1	2025-11-17 12:00:00.005845	\N	26
51	0.00	\N	\N		2025-11-17 10:45:33.866324	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_0a7b237b9cfb4e599fb4133a7a6eab65	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251117104533012b00d6	\N	\N	\N	\N	CANCELLED	1	2025-11-17 12:00:00.006898	\N	26
52	0.00	\N	\N		2025-11-17 10:45:35.419648	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_caddda2c27d345648703f255ff698a22	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511171045356f25305d	\N	\N	\N	\N	CANCELLED	1	2025-11-17 12:00:00.007133	\N	26
53	0.00	\N	\N	\N	2025-11-17 12:42:34.25673	INR	\N	TIMEOUT	Payment timed out	order_39e3276c3c334134a00985627c0f7de5	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251117124234533f28d3	\N	\N	\N	\N	CANCELLED	1	2025-11-17 14:00:00.004486	\N	41
54	0.00	\N	\N		2025-11-22 13:10:10.657339	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_ab4c8a4adb5540029bcac41884d63b93	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025112213101005b1de54	\N	\N	\N	\N	CANCELLED	3	2025-11-22 15:00:00.009587	\N	10
55	0.00	\N	\N		2025-11-22 13:10:11.617264	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_fb6d787df9ed4195b868eb8a3f6c806b	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025112213101149de69ef	\N	\N	\N	\N	CANCELLED	3	2025-11-22 15:00:00.009755	\N	10
56	0.00	\N	\N		2025-11-22 13:11:02.689627	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_9f74ef25fcb442069d39d56508d2be1e	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025112213110265b0b93b	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.009851	\N	10
57	0.00	\N	\N		2025-11-22 13:11:03.712774	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_2da6c81f4e414142962c8eb2255d5d56	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511221311035243a240	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.009887	\N	15
59	0.00	\N	\N		2025-11-22 13:11:10.164568	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_1bb9e67bc2d54fd3971ccc0276615466	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251122131110cea86081	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.009939	\N	10
60	0.00	\N	\N		2025-11-22 13:11:11.25504	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_daced79f53a840709df1027747976f46	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511221311112f254b02	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.009965	\N	15
61	0.00	\N	\N		2025-11-22 13:11:17.786988	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_4ae4f50e34844188a91957c55e45d722	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251122131117e5b393c6	\N	\N	\N	\N	CANCELLED	3	2025-11-22 15:00:00.010021	\N	15
62	0.00	\N	\N		2025-11-22 13:11:18.965678	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_8cf3999a53774ecfad42cf1a28c084c6	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251122131118651b5df0	\N	\N	\N	\N	CANCELLED	3	2025-11-22 15:00:00.01005	\N	15
63	0.00	\N	\N		2025-11-22 13:11:19.140147	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_649e61a6b556487ba2a0f82dd5bdfeae	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511221311199643525a	\N	\N	\N	\N	CANCELLED	3	2025-11-22 15:00:00.010074	\N	15
65	0.00	\N	\N		2025-11-22 13:11:19.54457	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_ad99ef28ae0440f3b580c7a0e6f7230e	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511221311191b88539b	\N	\N	\N	\N	CANCELLED	3	2025-11-22 15:00:00.010126	\N	15
66	0.00	\N	\N		2025-11-22 13:11:19.744161	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_8e2731de1da24a6dbc16aa8459080de8	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251122131119823ee371	\N	\N	\N	\N	CANCELLED	3	2025-11-22 15:00:00.010155	\N	15
67	0.00	\N	\N		2025-11-22 13:11:19.938071	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_4c29dde4321d47fb8aa48229afbfa695	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511221311190328db63	\N	\N	\N	\N	CANCELLED	3	2025-11-22 15:00:00.010184	\N	15
68	0.00	\N	\N		2025-11-22 13:11:20.093939	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_384447df3cd74e69a1594a0d9e643b89	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251122131120b1f2e3bb	\N	\N	\N	\N	CANCELLED	3	2025-11-22 15:00:00.010214	\N	15
70	0.00	\N	\N		2025-11-22 13:11:21.554631	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_3605ce6adb7a47ecb96b054da19d5f40	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511221311219bf98b30	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.010275	\N	15
71	0.00	\N	\N		2025-11-22 13:11:21.711421	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_58a1d1a90fe14e2286acb43789c45c9b	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511221311213be87288	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.010348	\N	15
72	0.00	\N	\N		2025-11-22 13:11:21.93403	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_ee96ce950ab74c11b4794100cc18de20	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251122131121b77b86f8	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.010381	\N	15
74	0.00	\N	\N		2025-11-22 13:12:03.636794	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_965ded142ba343cb87ff799861f12f26	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025112213120344452d21	\N	\N	\N	\N	CANCELLED	3	2025-11-22 15:00:00.012483	\N	15
75	0.00	\N	\N		2025-11-22 13:12:04.778607	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_2e6dd5ebaaf34b15aadc7c74aac691a0	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511221312044523bb3f	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.012555	\N	15
79	0.00	\N	\N		2025-11-22 13:12:18.309563	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_93992f11717841109a3a1cbc66ab048c	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025112213121894e30918	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.01263	\N	15
80	0.00	\N	\N		2025-11-22 13:12:18.548717	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_03b18d7ee9eb4283beb364939176396b	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511221312185a5c4949	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.012646	\N	15
64	0.00	\N	\N		2025-11-22 13:11:19.345641	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_12968c6afe49482b882df90013092a1f	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511221311197efaae61	\N	\N	\N	\N	CANCELLED	3	2025-11-22 15:00:00.010099	\N	15
69	0.00	\N	\N		2025-11-22 13:11:20.750576	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_77a04c5d08b44c1397a3e8200e17d955	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025112213112015f510ba	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.010246	\N	15
73	0.00	\N	\N		2025-11-22 13:12:00.711185	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_5dc3b778a4614ead83d7ccb94992d4b4	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251122131200cc9f04e5	\N	\N	\N	\N	CANCELLED	3	2025-11-22 15:00:00.010405	\N	15
78	0.00	\N	\N		2025-11-22 13:12:17.931851	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_ad5a0eefa9bc42749750bfe1b5721bb3	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511221312173a238fc7	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.012614	\N	15
58	0.00	\N	\N		2025-11-22 13:11:08.209062	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_403cc491646d4af8bfdb68682c5e7f3c	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251122131108db5b94dc	\N	\N	\N	\N	CANCELLED	3	2025-11-22 15:00:00.009915	\N	15
76	0.00	\N	\N		2025-11-22 13:12:13.803535	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_44bff32526d7465aae37ea54a89f62c9	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025112213121310d15c64	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.012579	\N	15
77	0.00	\N	\N		2025-11-22 13:12:16.968012	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_0aae55cf55044571bfc29fb3850d2e8b	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251122131216b1ffd101	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.012597	\N	15
81	0.00	\N	\N		2025-11-22 13:12:18.711534	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_245830e52975476daeb857ca2c9c297f	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251122131218421a41ee	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.012692	\N	15
82	0.00	\N	\N		2025-11-22 13:12:19.289645	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_172ffe72f1384752a07ebc748c141434	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025112213121910539100	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.01272	\N	15
83	0.00	\N	\N		2025-11-22 13:12:19.359439	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_d3c7268aaf5c4d02a22f3df4ea596769	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251122131219242c5d9b	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.01274	\N	15
84	0.00	\N	\N		2025-11-22 13:12:19.599655	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_3f17b6e733cf450bb9cbb763e6354bd3	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511221312197adf3b0c	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.012757	\N	15
85	0.00	\N	\N		2025-11-22 13:12:19.790945	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_480e9f51666f4b08870295d93b242f8a	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202511221312195dcd8794	\N	\N	\N	\N	CANCELLED	1	2025-11-22 15:00:00.012773	\N	15
86	0.00	\N	\N	\N	2025-12-02 17:36:09.003496	INR	\N	TIMEOUT	Payment timed out	order_6fa46dbc864d40cb9ad42ac5e76c2356	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202512021736099c31d1c1	\N	\N	\N	\N	CANCELLED	1	2025-12-02 19:00:00.003509	\N	64
87	0.00	\N	\N	\N	2025-12-04 05:43:26.321374	INR	\N	TIMEOUT	Payment timed out	order_58b220f62d0d4b73a4b783b697690657	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202512040543266b76d74d	\N	\N	\N	\N	CANCELLED	1	2025-12-04 07:00:00.004707	\N	68
88	0.00	\N	\N		2025-12-04 08:07:32.971937	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_6ee6dee9571f42d69da8eef3b848273f	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251204080732966abd1d	\N	\N	\N	\N	CANCELLED	3	2025-12-04 10:00:00.006926	\N	71
89	0.00	\N	\N		2025-12-04 08:07:43.865491	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_7089234422844e0aa162c4551e5e9d63	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP202512040807435311b218	\N	\N	\N	\N	CANCELLED	1	2025-12-04 10:00:00.007423	\N	71
90	0.00	\N	\N		2025-12-04 08:07:58.708036	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_39852cc89fc8489f9150473f22ce236c	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251204080758fcae6c0e	\N	\N	\N	\N	CANCELLED	1	2025-12-04 10:00:00.007549	\N	71
91	0.00	\N	\N		2025-12-05 07:58:36.58239	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_a611c0808db24a619f0a542c917df323	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP20251205075836649deae6	\N	\N	\N	\N	CANCELLED	1	2025-12-05 09:00:00.003371	\N	71
92	0.00	\N	\N		2025-12-05 08:00:04.335103	INR	Coupon applied: 	TIMEOUT	Payment timed out	order_bf395c8fec0c4ff69e9af1d465a61206	\N	\N	\N	0.00	\N	\N	\N	SUBSCRIPTION	\N	\N	RANPP2025120508000476411e30	\N	\N	\N	\N	CANCELLED	1	2025-12-05 10:00:00.013671	\N	71
\.


--
-- Data for Name: properties; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.properties (id, active, added_by_franchisee, address, agreement_accepted, approved, area, availability, bathrooms, bedrooms, city, created_at, description, district_name, district, featured, garage_size, garages, label, land_area, land_area_postfix, latitude, longitude, note, owner_permanent_id, permanent_id, pincode, place_name, price, private_note, renovated, scheduled_deletion, size_postfix, state, status, stock, street_number, subscription_expiry, subscription_id, title, type, unit_count, unit_type, updated_at, video_url, year_built, youtube_url, added_by_user_id, district_id, owner_id) FROM stdin;
2	t	f	123, near Aurbindo Hospital, Risi Nagar, Emarld City, Indore, Madhya Pradesh 452007, India	t	t	3000		2	1	Indore	2025-11-11 08:52:21.05486	have many facility with modern interiror	Indore	Indore	f	600	0	GOLDEN_OFFER	\N		22.7195687	75.8577258	perfect for family	RANPU202511051011242	RANP202511110852212	452007	near mr10 mall	900000.00		yes	\N	sqft	Madhya Pradesh	FOR_RENT	\N	130	2025-12-11 08:37:27.610718	8	new apartment	APARTMENT	\N	\N	2025-11-11 08:52:22.938569	\N	2019	\N	\N	332	2
1	t	f	123, near Aurbindo Hospital, Risi Nagar, Emarld City, Indore, Madhya Pradesh 452007, India	t	t	1200		1	2	Indore	2025-11-11 08:40:11.163737	villa with modern furniture 	Indore	Indore	t	500	0	HOT_OFFER	\N		22.7195687	75.8577258	perfect for a family	RANPU202511110656578	RANP202511110840111	452007	near city mall	700000.00		yes	\N	sqft	Madhya Pradesh	FOR_RENT	\N	123	2025-12-11 08:37:12.135874	7	luxuries villa 	VILLA	\N	\N	2025-11-13 11:52:14.526236	\N	2020	\N	\N	332	22
3	t	f	behind Apollo Premiere, Indore, Madhya Pradesh, India	t	t	2	Available	2	25	Indore	2025-11-14 08:48:55.473048	description	Indore	Indore	f	25	2	HOT_OFFER	0	\N	22.749518061614417	75.89936334639788	\N	RANPU2025111110382110	RANP202511140848553	452010	\N	22.00	\N	yes	\N	sq m	Madhya Pradesh	FOR_RENT	\N	\N	2025-12-11 10:40:35.392866	11	Title	HOUSE	\N	\N	2025-11-14 08:48:56.79904	\N	2004	\N	\N	332	26
11	t	f	WQHG+RW Haraj Khedi, Madhya Pradesh, India	t	t	12323333		1	2	Haraj Khedi	2025-11-15 09:20:35.776925	Big House 	Bhopal	Bhopal	f	\N	0	HOT_OFFER	\N		22.92958341867086	76.7772899142742	opposite gardeen	RANPU2025111508384511	RANP2025111509203511	466116	oppsoite gardeen	1200000.00			\N	sqmt	Madhya Pradesh	FOR_SALE	\N		2025-12-15 08:49:47.382555	15	luxurious house 	MULTI_FAMILY_HOME	\N	\N	2025-11-15 09:20:37.272211	\N	\N	\N	\N	319	28
21	t	f	Q3H5+4M Sanai Rampur, Madhya Pradesh, India	t	t	3233		0	0	Sanai Rampur	2025-11-15 13:39:26.725525	Spacious 2 BHK flat in the prime area of Wakad. The society has excellent amenities including gym, CCTV, security guard, and 24/7 water. Very close to Hinjewadi IT Park, Schools, Hospitals, and Phoenix Mall. Ideal for family or working professionals.	Bhopal	Bhopal	f	\N	0	GOLDEN_OFFER	\N		23.777775539591904	78.05915247120856	good	RANPU2025111513314914	RANP2025111513392621		Near Phoenix Mall & Hinjewadi Flyover	21878876.00			\N	sqmt	Madhya Pradesh	FOR_SALE	\N		2025-12-15 13:33:42.051988	18	2 BHK Semi-Furnished Apartment in Wakad, Pune	COMMERCIAL	\N	\N	2025-11-15 13:39:28.22696	\N	\N	\N	\N	319	31
31	t	f	123, near Aurbindo Hospital, Risi Nagar, Emarld City, Indore, Madhya Pradesh 452007, India	t	t	1500		1	2	Indore	2025-11-17 12:44:07.535607	2bhk	Indore	Indore	f	\N	0	GOLDEN_OFFER	\N		22.7195687	75.8577258	must buy	RANPU202511171127341	RANP202511171244071	452007	near c21	200000.00			\N	sqft	Madhya Pradesh	FOR_RENT	\N		2025-12-17 12:42:34.903011	28	2 bhk	SINGLE_FAMILY_HOME	\N	\N	2025-11-17 12:44:09.427742	\N	\N	\N	\N	332	41
25	t	f	VMPW+PP Bramhani, Maharashtra, India	t	t	12222		2	2	Bramhani	2025-11-16 04:31:45.696817	Independent villa with garden, 24x7 security	Nagpur	Nagpur	f	\N	0	HOT_OFFER	\N		20.886772350328716	78.69676719822883	good	RANPU2025111604282416	RANP2025111604314525	442104	Near Mall	500000000.00			\N	sqmt	Maharashtra	FOR_SALE	\N		2025-12-16 04:29:25.251258	20	3 BHK Villa	MULTI_FAMILY_HOME	\N	\N	2025-11-16 04:31:47.538965	\N	\N	\N	\N	381	33
29	t	f	MFW7+5Q Khejra Misar, Madhya Pradesh, India	t	t	1209		3	5	Khejra Misar	2025-11-17 07:09:40.144298	Have Full Facillity 	Bhopal	Bhopal	f	\N	0	HOT_OFFER	\N		23.69544105486957	77.4644483945608	add property	RANPU202511170705101	RANP202511170709401	463111	Near Mall	12000000.00			\N	sqmt	Madhya Pradesh	FOR_SALE	\N		2025-12-17 07:07:09.798004	23	Property On Big Sale 	MULTI_FAMILY_HOME	\N	\N	2025-11-17 07:09:41.710247	\N	\N	\N	\N	319	36
30	t	f	C468+H8P, Hemra Etwa Road, Kutumb Nagar, Refinery Twp, Begusarai, Bihar 851117, India	t	t	800		2	3	Begusarai	2025-11-17 08:39:09.615314	Spacious Apartment with modern aminities.	Begusarai	Begusarai	f	\N	0	GOLDEN_OFFER	\N		25.411341334827686	86.11526570820176	Perfect for family	RANPU202511090614496	RANP202511170839092	851117	Near Jagdeo Baba Asthan	25000.00			\N	sqft	Bihar	FOR_RENT	\N		2025-12-17 08:32:45.068952	24	Luxurious 3BHK FLAT	APARTMENT	\N	\N	2025-11-17 08:39:11.571578	\N	\N	\N	\N	75	15
32	t	f	2VP9+FM Khojampur, Madhya Pradesh, India	t	t	1288		0	0	Khojampur	2025-12-02 17:38:22.050498	i wnat to add property	Sagar	Sagar	f	\N	0	HOT_OFFER	\N		24.03613767335635	78.86918495812415	i want to add 	RANPU2025120217343516	RANP202512021738222	470335	opposite gardeen	1200000.00			\N	sqft	Madhya Pradesh	FOR_SALE	\N		2026-01-01 17:36:09.551095	31	big house	COMMERCIAL	\N	\N	2025-12-02 17:38:22.822988	\N	\N	\N	\N	349	64
\.


--
-- Data for Name: property_additional_details; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.property_additional_details (property_id, value, title) FROM stdin;
\.


--
-- Data for Name: property_amenities; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.property_amenities (property_id, amenity) FROM stdin;
\.


--
-- Data for Name: property_features; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.property_features (property_id, feature) FROM stdin;
1	GARDEN
1	MODULAR_KITCHEN
1	WIFI
1	ELECTRICITY_CONNECTION
1	SWIMMING_POOL
2	GARDEN
2	HARDWOOD_FLOORS
2	MODULAR_KITCHEN
2	LAUNDRY
2	PARKING
2	ELECTRICITY_CONNECTION
3	Smart Home
3	Swimming Pool
3	Gym
3	Security System
11	STORAGE
11	GARDEN
21	GARDEN
21	SECURITY
21	PARKING
21	ELECTRICITY_CONNECTION
25	BALCONY
25	GARDEN
25	SECURITY
25	ELECTRICITY_CONNECTION
29	WATER_SUPPLY
29	ELECTRICITY_CONNECTION
30	BALCONY
30	WATER_SUPPLY
30	GARDEN
30	SECURITY
30	AIR_CONDITIONING
30	LIFT
30	MODULAR_KITCHEN
30	LAUNDRY
30	PARKING
30	WIFI
30	ELECTRICITY_CONNECTION
31	GARDEN
31	PARKING
31	ELECTRICITY_CONNECTION
32	SECURITY
32	PARKING
\.


--
-- Data for Name: property_images; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.property_images (property_id, image_url, image_order) FROM stdin;
1	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/properties/user/22_arpita/1_luxuries-villa/1-luxuries-villa-3f5c153e-32ce-47ad-9dee-7cb5329340d5.jpg	0
2	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/properties/user/2_shahnawaz/2_new-apartment/2-new-apartment-d1c079c5-af57-4675-aeb5-268ceaa84f53.jpg	0
3	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/properties/user/26_abhishek-kachhawa/3_title/3-title-de98eebd-61ed-4870-953a-1a4596f1c195.png	0
11	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/properties/user/28_lomash/11_luxurious-house/11-luxurious-house-638efb61-825c-4ff5-a666-89fdc559f774.jpg	0
21	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/properties/user/31_hunen/21_2-bhk-semi-furnished-apartment-in-wakad-pune/21-2-bhk-semi-furnished-apartment-in-wakad-pune-d1d67254-1ab1-4219-b661-47d59515efa9.jpg	0
25	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/properties/user/33_amaan/25_3-bhk-villa/25-3-bhk-villa-17b1b6be-80c7-43c5-8154-4a6954a9fd93.jpg	0
29	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/properties/user/36_vinayak/29_property-on-big-sale/29-property-on-big-sale-a274ce3b-aa97-4252-8c0e-6352c2c5ef0e.jpg	0
30	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/properties/user/15_vikram/30_luxurious-3bhk-flat/30-luxurious-3bhk-flat-4d25d9df-e45e-4548-b65e-6fc62c41c661.jpg	0
30	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/properties/user/15_vikram/30_luxurious-3bhk-flat/30-luxurious-3bhk-flat-c0256c19-ed46-4125-ac4f-b6602f7a8a9a.jpg	1
30	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/properties/user/15_vikram/30_luxurious-3bhk-flat/30-luxurious-3bhk-flat-a61a35e9-3808-4ed8-a92e-634bac599edd.jpg	2
30	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/properties/user/15_vikram/30_luxurious-3bhk-flat/30-luxurious-3bhk-flat-3137e6dc-b8a5-44fb-8cd2-d045fe7f8408.jpg	3
30	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/properties/user/15_vikram/30_luxurious-3bhk-flat/30-luxurious-3bhk-flat-3d23ec33-ee4c-4bff-b8f7-bdd2a07d555d.jpg	4
31	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/properties/user/41_chirag-sir/31_2-bhk/31-2-bhk-0c275c55-9b38-4721-93a4-94a461d4ba6e.jpg	0
32	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/properties/user/64_abc/32_big-house/32-big-house-700e70c2-2737-4aac-8273-4d8217325339.jpeg	0
\.


--
-- Data for Name: property_inquiries; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.property_inquiries (id, area, bathrooms, bedrooms, city, created_at, district_id, email, info_type, last_updated_at, latitude, longitude, max_price, message, min_size, mobile_number, name, property_type, state, status, zip_code) FROM stdin;
\.


--
-- Data for Name: property_inquiry_status_history; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.property_inquiry_status_history (id, comment, status, updated_at, updated_by, inquiry_id) FROM stdin;
\.


--
-- Data for Name: property_luxurious_features; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.property_luxurious_features (property_id, luxurious_feature) FROM stdin;
\.


--
-- Data for Name: property_reels; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.property_reels (id, city, comment_count, created_at, description, district, duration_seconds, file_size, latitude, like_count, longitude, payment_required, payment_transaction_id, processing_status, public_id, save_count, share_count, state, status, thumbnail_url, title, updated_at, video_url, view_count, user_id, property_id) FROM stdin;
6	Indore	0	2025-11-14 12:12:49.955442	\N	Indore	30	5729752	22.749518061614417	0	75.89936334639788	t	\N	COMPLETED	7caca1bb-d949-4c6b-89fc-534e9be5affb	0	0	Madhya Pradesh	DRAFT	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/media/thumbnails/user/26_abhishek-kachhawa/3_title/3-bdjjdjdjh-7571da44-c639-4959-8df6-ffbe9b798770jpg	bdjjdjdjh	2025-11-14 12:12:49.955447	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/media/reels/user/26_abhishek-kachhawa/3_title/3-bdjjdjdjh-7571da44-c639-4959-8df6-ffbe9b798770.mp4	0	26	3
7	Indore	0	2025-11-14 12:19:50.421619	\N	Indore	30	5729752	22.749518061614417	0	75.89936334639788	t	\N	COMPLETED	c233821b-34ba-4439-9993-8644e99b133b	0	0	Madhya Pradesh	DRAFT	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/media/thumbnails/user/26_abhishek-kachhawa/3_title/3-fyhhhh-53ea2ea7-2d68-4829-96d0-991c03e507c6jpg	fyhhhh	2025-11-14 12:19:50.421624	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/media/reels/user/26_abhishek-kachhawa/3_title/3-fyhhhh-53ea2ea7-2d68-4829-96d0-991c03e507c6.mp4	0	26	3
2	Indore	0	2025-11-11 08:53:52.865471	\N	Indore	30	306761	22.7195687	2	75.8577258	f	\N	COMPLETED	4cb56f91-1976-47d0-a54c-fb45434b8486	0	0	Madhya Pradesh	PUBLISHED	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/media/thumbnails/user/2_shahnawaz/2_new-apartment/2-explore-f919c9f9-6c41-4fab-8b5d-a284754818dbjpg	explore	2025-12-03 10:55:19.479869	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/media/reels/user/2_shahnawaz/2_new-apartment/2-explore-f919c9f9-6c41-4fab-8b5d-a284754818db.mp4	9	2	2
1	Indore	0	2025-11-11 08:44:32.276991	\N	Indore	30	538107	22.7195687	2	75.8577258	f	\N	COMPLETED	7a29702d-3906-4cd5-9a00-333459b37d48	0	0	Madhya Pradesh	PUBLISHED	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/media/thumbnails/user/22_arpita/1_luxuries-villa/1-new-adbd0004-4585-48b1-96f6-3df3f809d465jpg	new	2025-12-03 10:55:23.522933	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/media/reels/user/22_arpita/1_luxuries-villa/1-new-adbd0004-4585-48b1-96f6-3df3f809d465.mp4	9	22	1
3	Indore	0	2025-11-14 12:04:15.101125	\N	Indore	30	5080875	22.749518061614417	0	75.89936334639788	f	\N	COMPLETED	2bd88e5a-4399-4393-a1a7-07e2bd06d1d7	0	0	Madhya Pradesh	PUBLISHED	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/media/thumbnails/user/26_abhishek-kachhawa/3_title/3-property-by-abhishek-kachhawa-ff1d437b-f66b-4912-b4f5-4f0c9edb5f98jpg	property by Abhishek Kachhawa	2025-12-03 10:55:30.199464	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/media/reels/user/26_abhishek-kachhawa/3_title/3-property-by-abhishek-kachhawa-ff1d437b-f66b-4912-b4f5-4f0c9edb5f98.mp4	5	26	3
4	Indore	0	2025-11-14 12:12:17.640751	\N	Indore	30	5729752	22.749518061614417	0	75.89936334639788	t	\N	COMPLETED	b1ba2127-9fde-41ec-b207-d282c746c8a3	0	0	Madhya Pradesh	DRAFT	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/media/thumbnails/user/26_abhishek-kachhawa/3_title/3-property-by-abhishek-kachhawa-developer-ebaf26cd-8079-4a76-94d4-23457353a686jpg	property by Abhishek Kachhawa developer	2025-11-14 12:12:17.640757	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/media/reels/user/26_abhishek-kachhawa/3_title/3-property-by-abhishek-kachhawa-developer-ebaf26cd-8079-4a76-94d4-23457353a686.mp4	0	26	3
5	Indore	0	2025-11-14 12:12:18.04665	\N	Indore	30	5729752	22.749518061614417	0	75.89936334639788	t	\N	COMPLETED	34fd6218-838d-4836-840c-0fd017f38e1b	0	0	Madhya Pradesh	DRAFT	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/media/thumbnails/user/26_abhishek-kachhawa/3_title/3-property-by-abhishek-kachhawa-developer-391e6af4-8bf2-4b3a-aa44-7a291fb9af52jpg	property by Abhishek Kachhawa developer	2025-11-14 12:12:18.046654	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/media/reels/user/26_abhishek-kachhawa/3_title/3-property-by-abhishek-kachhawa-developer-391e6af4-8bf2-4b3a-aa44-7a291fb9af52.mp4	0	26	3
\.


--
-- Data for Name: property_reviews; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.property_reviews (id, comment, created_at, rating, updated_at, property_id, user_id) FROM stdin;
1	hjjsjslk	2025-11-15 05:49:22.973409	4	2025-11-15 05:49:22.973412	2	26
2	Good Better Best	2025-11-15 05:50:29.599114	2	2025-11-15 05:50:29.599116	3	26
\.


--
-- Data for Name: property_security_features; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.property_security_features (property_id, security_feature) FROM stdin;
\.


--
-- Data for Name: property_update_fields; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.property_update_fields (update_request_id, old_value, field_name) FROM stdin;
\.


--
-- Data for Name: property_update_new_fields; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.property_update_new_fields (update_request_id, new_value, field_name) FROM stdin;
\.


--
-- Data for Name: property_update_requests; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.property_update_requests (id, admin_approved, admin_notes, admin_reviewed, admin_reviewed_at, district, franchisee_approved, franchisee_notes, is_franchisee_request, franchisee_reviewed, franchisee_reviewed_at, rejection_reason, request_id, request_notes, status, submitted_at, updated_at, franchisee_id, property_id, requested_by, reviewed_by_admin, reviewed_by_franchisee) FROM stdin;
\.


--
-- Data for Name: property_visits; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.property_visits (id, created_at, notes, scheduled_time, status, updated_at, property_id, user_id) FROM stdin;
1	2025-11-14 13:01:46.381576	hy	2025-11-22 18:31:00	CONFIRMED	2025-11-14 13:02:05.45642	3	23
2	2025-11-15 08:53:56.139341	helllo	2025-11-21 15:23:00	PENDING	2025-11-15 08:53:56.139343	2	26
3	2025-11-15 08:55:18.360257	Abhimanyu Kumawat and u from dating in my life is a software developer in your profile and u are you doing right now because this is the day of the day of	2025-11-20 17:24:00	PENDING	2025-11-15 08:55:18.360261	3	26
4	2025-12-04 08:12:26.148966		2025-12-05 13:42:00	PENDING	2025-12-04 08:12:26.148979	11	71
\.


--
-- Data for Name: recent_viewed_properties; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.recent_viewed_properties (id, viewed_at, property_id, user_id) FROM stdin;
\.


--
-- Data for Name: reel_interactions; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.reel_interactions (id, comment, created_at, type, reel_id, user_id) FROM stdin;
1	\N	2025-11-11 08:44:55.08458	VIEW	1	22
2	\N	2025-11-11 08:53:16.294171	VIEW	1	2
4	\N	2025-11-11 10:25:53.97582	LIKE	1	13
5	\N	2025-11-11 10:25:56.871049	LIKE	2	13
6	\N	2025-11-13 06:50:59.425455	LIKE	1	24
7	\N	2025-11-13 12:45:26.104441	VIEW	2	23
8	\N	2025-11-13 12:45:27.641199	VIEW	1	23
9	\N	2025-11-14 12:22:25.37386	VIEW	3	23
10	\N	2025-11-17 12:50:26.444281	VIEW	2	41
11	\N	2025-11-17 12:50:29.437809	VIEW	1	41
12	\N	2025-11-17 12:50:29.478272	VIEW	3	41
13	\N	2025-11-19 15:38:40.849753	VIEW	2	36
14	\N	2025-11-19 15:39:19.12409	LIKE	2	36
15	\N	2025-11-19 15:39:51.582892	VIEW	1	36
16	\N	2025-11-19 15:40:32.629316	VIEW	3	36
17	\N	2025-12-03 10:55:19.476823	VIEW	2	1
18	\N	2025-12-03 10:55:23.521997	VIEW	1	1
19	\N	2025-12-03 10:55:30.198313	VIEW	3	1
\.


--
-- Data for Name: review_likes; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.review_likes (id, created_at, review_id, user_id) FROM stdin;
\.


--
-- Data for Name: role_request_documents; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.role_request_documents (role_request_id, document_url) FROM stdin;
\.


--
-- Data for Name: role_requests; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.role_requests (id, admin_comment, comment, created_at, processed_at, reason, requested_role, status, updated_at, processed_by, user_id) FROM stdin;
1	Auto-approved by system	\N	2025-11-05 11:40:55.311158	2025-11-05 11:40:55.307628	i wnat to add Property	SELLER	APPROVED	2025-11-05 11:40:55.311178	\N	2
2	Auto-approved by system	\N	2025-11-05 12:28:46.701047	2025-11-05 12:28:46.700604	mjjbkb	SELLER	APPROVED	2025-11-05 12:28:46.701059	\N	10
3	Auto-approved by system	\N	2025-11-11 06:58:04.704243	2025-11-11 06:58:04.703996	i	SELLER	APPROVED	2025-11-11 06:58:04.70425	\N	22
4	Auto-approved by system	\N	2025-11-11 08:28:25.914984	2025-11-11 08:28:25.91471	ee	DEVELOPER	APPROVED	2025-11-11 08:28:25.914988	\N	22
5	Auto-approved by system	\N	2025-11-11 08:49:15.399492	2025-11-11 08:49:15.399243	ff	ADVISOR	APPROVED	2025-11-11 08:49:15.399496	\N	2
6	Auto-approved by system	\N	2025-11-11 08:58:21.841936	2025-11-11 08:58:21.841672	v	DEVELOPER	APPROVED	2025-11-11 08:58:21.841938	\N	25
7	Auto-approved by system	\N	2025-11-11 09:06:53.145384	2025-11-11 09:06:53.145155	h	ADVISOR	APPROVED	2025-11-11 09:06:53.145388	\N	25
8	Auto-approved by system	\N	2025-11-11 09:19:04.319542	2025-11-11 09:19:04.319179	k	ADVISOR	APPROVED	2025-11-11 09:19:04.319545	\N	13
9	Auto-approved by system	\N	2025-11-11 10:29:34.39463	2025-11-11 10:29:34.394293	s	DEVELOPER	APPROVED	2025-11-11 10:29:34.394634	\N	13
10	Auto-approved by system	\N	2025-11-11 10:39:09.443297	2025-11-11 10:39:09.442998	f	DEVELOPER	APPROVED	2025-11-11 10:39:09.443303	\N	26
11	Auto-approved by system	\N	2025-11-13 12:05:33.629288	2025-11-13 12:05:33.629048	hy	DEVELOPER	APPROVED	2025-11-13 12:05:33.629292	\N	23
12	Auto-approved by system	\N	2025-11-13 12:05:54.42784	2025-11-13 12:05:54.427519	fewf	SELLER	APPROVED	2025-11-13 12:05:54.427844	\N	23
13	Auto-approved by system	\N	2025-11-13 12:06:05.983272	2025-11-13 12:06:05.982965	fwef	ADVISOR	APPROVED	2025-11-13 12:06:05.983276	\N	23
14	Auto-approved by system	\N	2025-11-14 08:40:18.380913	2025-11-14 08:40:18.380569	dftygf	SELLER	APPROVED	2025-11-14 08:40:18.380917	\N	26
15	Auto-approved by system	\N	2025-11-14 13:36:44.824135	2025-11-14 13:36:44.823949	asgk	ADVISOR	APPROVED	2025-11-14 13:36:44.824177	\N	26
16	Auto-approved by system	\N	2025-11-15 08:41:12.303436	2025-11-15 08:41:12.303036	i  want to make seller	SELLER	APPROVED	2025-11-15 08:41:12.303437	\N	28
17	Auto-approved by system	\N	2025-11-15 09:47:33.234139	2025-11-15 09:47:33.233897	bj	SELLER	APPROVED	2025-11-15 09:47:33.234142	\N	29
18	Auto-approved by system	\N	2025-11-15 09:56:24.571784	2025-11-15 09:56:24.571578	Seller	SELLER	APPROVED	2025-11-15 09:56:24.571786	\N	30
19	Auto-approved by system	\N	2025-11-15 13:21:41.150992	2025-11-15 13:21:41.150819	i want to add	DEVELOPER	APPROVED	2025-11-15 13:21:41.150994	\N	30
20	Auto-approved by system	\N	2025-11-15 13:32:51.165479	2025-11-15 13:32:51.165303	add property	DEVELOPER	APPROVED	2025-11-15 13:32:51.165482	\N	31
21	Auto-approved by system	\N	2025-11-15 13:37:09.078146	2025-11-15 13:37:09.077949	seller	SELLER	APPROVED	2025-11-15 13:37:09.078149	\N	31
22	Auto-approved by system	\N	2025-11-16 02:23:51.812913	2025-11-16 02:23:51.812712	susy	SELLER	APPROVED	2025-11-16 02:23:51.812915	\N	12
23	Auto-approved by system	\N	2025-11-16 04:18:00.373538	2025-11-16 04:18:00.373315	i want to add property	SELLER	APPROVED	2025-11-16 04:18:00.373541	\N	32
24	Auto-approved by system	\N	2025-11-16 04:29:13.972872	2025-11-16 04:29:13.972697	i want to add property	SELLER	APPROVED	2025-11-16 04:29:13.972874	\N	33
25	Auto-approved by system	\N	2025-11-16 04:34:13.929749	2025-11-16 04:34:13.929594	i want to add property	DEVELOPER	APPROVED	2025-11-16 04:34:13.929751	\N	34
26	Auto-approved by system	\N	2025-11-16 08:59:00.953945	2025-11-16 08:59:00.953768	i want to add Property	SELLER	APPROVED	2025-11-16 08:59:00.953947	\N	35
27	Auto-approved by system	\N	2025-11-17 07:06:29.684682	2025-11-17 07:06:29.683971	i want to add property	SELLER	APPROVED	2025-11-17 07:06:29.684692	\N	36
28	Auto-approved by system	\N	2025-11-17 08:32:01.556999	2025-11-17 08:32:01.556561	I want sell my properties.	SELLER	APPROVED	2025-11-17 08:32:01.557013	\N	15
29	Auto-approved by system	\N	2025-11-17 12:42:24.080468	2025-11-17 12:42:24.079779	sqa	SELLER	APPROVED	2025-11-17 12:42:24.080478	\N	41
30	Auto-approved by system	\N	2025-11-20 07:21:39.40761	2025-11-20 07:21:39.406381	d	DEVELOPER	APPROVED	2025-11-20 07:21:39.407614	\N	41
31	Auto-approved by system	\N	2025-11-20 07:28:00.996336	2025-11-20 07:28:00.996125	ff	ADVISOR	APPROVED	2025-11-20 07:28:00.996338	\N	41
32	Auto-approved by system	\N	2025-12-02 17:35:47.317039	2025-12-02 17:35:47.315781	i want to add property	SELLER	APPROVED	2025-12-02 17:35:47.317042	\N	64
33	Auto-approved by system	\N	2025-12-04 05:42:35.486176	2025-12-04 05:42:35.484199	i want to add property	DEVELOPER	APPROVED	2025-12-04 05:42:35.486187	\N	68
34	Auto-approved by system	\N	2025-12-04 08:07:15.362053	2025-12-04 08:07:15.361749	ghh	SELLER	APPROVED	2025-12-04 08:07:15.362061	\N	71
40	Auto-approved by system	\N	2025-12-08 03:27:26.294479	2025-12-08 03:27:26.294201	I want to list my properties for sale	ADMIN	APPROVED	2025-12-08 03:27:26.294485	\N	78
41	Auto-approved by system	\N	2025-12-08 03:34:21.174488	2025-12-08 03:34:21.174209	I want to list my properties for sale	ADMIN	APPROVED	2025-12-08 03:34:21.174495	\N	79
\.


--
-- Data for Name: sub_admin_permission; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.sub_admin_permission (id, action, module, subadmin_id) FROM stdin;
11	CREATE	ADVERTISEMENT	78
12	DELETE	ADVERTISEMENT	78
13	UPDATE	ADVERTISEMENT	78
14	VIEW	ADVERTISEMENT	78
15	CREATE	PROPERTY	78
16	UPDATE	PROPERTY	78
17	DELETE	PROPERTY	78
21	VIEW	FRANCHISEE	36
22	CREATE	FRANCHISEE	36
23	VIEW	SUBSCRIPTION	78
24	CREATE	SUBSCRIPTION	78
25	UPDATE	SUBSCRIPTION	78
26	DELETE	SUBSCRIPTION	78
27	VIEW	SUBSCRIPTION	36
28	CREATE	PROPERTY	36
29	DELETE	PROPERTY	36
30	UPDATE	PROPERTY	36
31	VIEW	PROPERTY	36
\.


--
-- Data for Name: sub_admins; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.sub_admins (id, name, email, mobile_number) FROM stdin;
1	Kaif Mansuri	kaif@gmail.com	9575657782
\.


--
-- Data for Name: subscription_plan_features; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.subscription_plan_features (id, allowed_video_formats, created_at, is_active, max_properties, max_reel_duration_seconds, max_reel_file_size_mb, max_reels_per_property, max_total_reels, monthly_price, plan_name, plan_type, updated_at) FROM stdin;
\.


--
-- Data for Name: subscription_plans; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.subscription_plans (id, active, content_delete_after_days, content_hide_after_days, created_at, description, duration_days, marketing_fee, max_properties, max_reels_per_property, max_total_reels, name, price, type, updated_at) FROM stdin;
1	t	90	60	2025-11-11 08:25:01.579893	Free Property Listing Plan	30	0.00	1	1	1	Basic Property Listing	0.00	SELLER	2025-11-19 10:51:53.153612
3	t	60	30	2025-11-19 11:14:47.577779	Basic Property Listing	30	0.00	1	1	1	Starter (Free)	0.00	PROPERTY	2025-11-19 11:38:59.259225
6	f	60	30	2025-11-19 12:33:08.423401	sekjefbhslkbhf	30	500.00	1	1	1	tsfus	500.00	PROPERTY	2025-11-19 12:35:35.375993
5	f	50	30	2025-11-19 12:21:29.125937	vmhbj	30	500.00	1	1	2	Basic 1	500.00	PROPERTY	2025-11-19 12:35:58.035888
2	f	60	30	2025-11-19 10:53:18.044991	This plan testing 	30	999.00	1	1	1	New	799.00	PROPERTY	2025-11-19 13:09:47.416689
4	f	60	30	2025-11-19 11:40:26.723531	basic Bundle	30	500.00	60	1	1	Basic Bundle	500.00	ADVISOR	2025-11-19 13:09:54.098424
\.


--
-- Data for Name: subscriptions; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.subscriptions (id, auto_renew, cancelled_at, content_deleted_at, content_hidden_at, coupon_code, created_at, discount_amount, district_id, end_date, is_renewal, marketing_fee, original_price, payment_confirmed, payment_reference, previous_subscription_id, price, start_date, status, updated_at, coupon_id, plan_id, user_id) FROM stdin;
7	f	\N	\N	\N	\N	2025-11-11 08:37:12.136063	\N	332	2025-12-11 08:37:12.135874	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-11-11 08:37:12.135874	ACTIVE	2025-11-11 08:37:12.136067	\N	1	22
8	f	\N	\N	\N	\N	2025-11-11 08:37:27.610918	\N	332	2025-12-11 08:37:27.610718	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-11-11 08:37:27.610717	ACTIVE	2025-11-11 08:37:27.610923	\N	1	2
9	f	\N	\N	\N	\N	2025-11-11 09:07:26.93969	\N	332	2025-12-11 09:07:26.939472	f	\N	0.00	\N	pay_ReNTphk6dIhLjR	\N	0.00	2025-11-11 09:07:26.939472	ACTIVE	2025-11-11 09:07:26.939694	\N	1	25
10	f	\N	\N	\N	\N	2025-11-11 10:28:46.052241	\N	332	2025-12-11 10:28:46.051991	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-11-11 10:28:46.05199	ACTIVE	2025-11-11 10:28:46.052244	\N	1	13
11	f	\N	\N	\N	\N	2025-11-11 10:40:35.393049	\N	332	2025-12-11 10:40:35.392866	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-11-11 10:40:35.392865	ACTIVE	2025-11-11 10:40:35.393051	\N	1	26
12	f	\N	\N	\N	\N	2025-11-14 11:52:19.454569	\N	332	2025-12-14 11:52:19.454315	f	\N	0.00	\N		\N	0.00	2025-11-14 11:52:19.454314	ACTIVE	2025-11-14 11:52:19.454573	\N	1	26
13	f	\N	\N	\N	\N	2025-11-14 11:54:21.311132	\N	332	2025-12-14 11:54:21.310943	f	\N	0.00	\N		\N	0.00	2025-11-14 11:54:21.310943	ACTIVE	2025-11-14 11:54:21.311136	\N	1	26
14	f	\N	\N	\N	\N	2025-11-15 08:29:46.216824	\N	332	2025-12-15 08:29:46.216612	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-11-15 08:29:46.216611	ACTIVE	2025-11-15 08:29:46.216826	\N	1	23
15	f	\N	\N	\N	\N	2025-11-15 08:49:47.382742	\N	332	2025-12-15 08:49:47.382555	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-11-15 08:49:47.382555	ACTIVE	2025-11-15 08:49:47.382745	\N	1	28
16	f	\N	\N	\N	\N	2025-11-15 09:47:50.734453	\N	332	2025-12-15 09:47:50.734292	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-11-15 09:47:50.734291	ACTIVE	2025-11-15 09:47:50.734455	\N	1	29
17	f	\N	\N	\N	\N	2025-11-15 13:18:33.882029	\N	332	2025-12-15 13:18:33.881886	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-11-15 13:18:33.881886	ACTIVE	2025-11-15 13:18:33.882032	\N	1	30
18	f	\N	\N	\N	\N	2025-11-15 13:33:42.052167	\N	332	2025-12-15 13:33:42.051988	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-11-15 13:33:42.051988	ACTIVE	2025-11-15 13:33:42.05217	\N	1	31
19	f	\N	\N	\N	\N	2025-11-16 04:18:34.104171	\N	332	2025-12-16 04:18:34.103903	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-11-16 04:18:34.103903	ACTIVE	2025-11-16 04:18:34.104174	\N	1	32
20	f	\N	\N	\N	\N	2025-11-16 04:29:25.2514	\N	332	2025-12-16 04:29:25.251258	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-11-16 04:29:25.251257	ACTIVE	2025-11-16 04:29:25.251402	\N	1	33
21	f	\N	\N	\N	\N	2025-11-16 04:35:13.153838	\N	332	2025-12-16 04:35:13.153668	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-11-16 04:35:13.153668	ACTIVE	2025-11-16 04:35:13.15384	\N	1	34
22	f	\N	\N	\N	\N	2025-11-16 08:59:14.987326	\N	332	2025-12-16 08:59:14.987207	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-11-16 08:59:14.987206	ACTIVE	2025-11-16 08:59:14.987327	\N	1	35
23	f	\N	\N	\N	\N	2025-11-17 07:07:09.798618	\N	332	2025-12-17 07:07:09.798004	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-11-17 07:07:09.797998	ACTIVE	2025-11-17 07:07:09.798632	\N	1	36
24	f	\N	\N	\N	\N	2025-11-17 08:32:45.069517	\N	332	2025-12-17 08:32:45.068952	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-11-17 08:32:45.06895	ACTIVE	2025-11-17 08:32:45.069527	\N	1	15
25	f	\N	\N	\N	\N	2025-11-17 10:44:20.428254	\N	332	2025-12-17 10:44:20.428031	f	\N	0.00	\N		\N	0.00	2025-11-17 10:44:20.42803	ACTIVE	2025-11-17 10:44:20.42826	\N	1	26
26	f	\N	\N	\N	\N	2025-11-17 10:45:35.282866	\N	332	2025-12-17 10:45:35.282565	f	\N	0.00	\N		\N	0.00	2025-11-17 10:45:35.282563	ACTIVE	2025-11-17 10:45:35.282875	\N	1	26
27	f	\N	\N	\N	\N	2025-11-17 10:45:36.250791	\N	332	2025-12-17 10:45:36.250572	f	\N	0.00	\N		\N	0.00	2025-11-17 10:45:36.250569	ACTIVE	2025-11-17 10:45:36.250798	\N	1	26
28	f	\N	\N	\N	\N	2025-11-17 12:42:34.903665	\N	332	2025-12-17 12:42:34.903011	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-11-17 12:42:34.903008	ACTIVE	2025-11-17 12:42:34.903677	\N	1	41
31	f	\N	\N	\N	\N	2025-12-02 17:36:09.552796	\N	332	2026-01-01 17:36:09.551095	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-12-02 17:36:09.551094	ACTIVE	2025-12-02 17:36:09.552798	\N	1	64
32	f	\N	\N	\N	\N	2025-12-04 05:43:26.983299	\N	332	2026-01-03 05:43:26.981571	f	\N	0.00	\N	FREE_PLAN	\N	0.00	2025-12-04 05:43:26.98156	ACTIVE	2025-12-04 05:43:26.98331	\N	1	68
\.


--
-- Data for Name: user_favorites; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.user_favorites (user_id, property_id) FROM stdin;
26	1
41	1
\.


--
-- Data for Name: user_following; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.user_following (id, created_at, followed_id, follower_id) FROM stdin;
1	2025-11-19 15:39:02.896773	2	36
\.


--
-- Data for Name: user_preferences; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.user_preferences (user_id, app_notifications, created_at, currency, dashboard_view, date_format, distance_unit, email_notifications, language, property_view_type, sms_notifications, temperature_unit, theme, time_format, updated_at) FROM stdin;
\.


--
-- Data for Name: user_roles; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.user_roles (user_id, role) FROM stdin;
1	USER
2	USER
2	ADMIN
10	USER
11	USER
12	USER
13	USER
14	USER
15	USER
17	USER
23	USER
24	USER
24	ADMIN
22	USER
25	USER
26	USER
28	USER
29	USER
30	USER
31	USER
32	USER
33	USER
34	USER
35	USER
36	USER
37	USER
38	USER
39	USER
40	USER
41	USER
42	USER
43	USER
46	USER
47	USER
48	USER
49	USER
50	USER
51	USER
53	USER
54	USER
54	ADMIN
56	USER
61	USER
62	USER
63	USER
64	USER
65	USER
66	USER
67	USER
68	USER
69	USER
70	USER
71	USER
2	SUBADMIN
10	SUBADMIN
22	SUBADMIN
22	SUBADMIN
22	SUBADMIN
2	SUBADMIN
25	SUBADMIN
25	SUBADMIN
13	SUBADMIN
13	SUBADMIN
26	SUBADMIN
26	SUBADMIN
23	SUBADMIN
23	SUBADMIN
23	SUBADMIN
26	SUBADMIN
26	SUBADMIN
28	SUBADMIN
29	SUBADMIN
30	SUBADMIN
30	SUBADMIN
31	SUBADMIN
31	SUBADMIN
12	SUBADMIN
32	SUBADMIN
33	SUBADMIN
34	SUBADMIN
35	SUBADMIN
36	SUBADMIN
15	SUBADMIN
41	SUBADMIN
41	SUBADMIN
41	SUBADMIN
64	SUBADMIN
68	SUBADMIN
71	SUBADMIN
76	SUBADMIN
77	SUBADMIN
77	USER
78	SUBADMIN
78	USER
78	ADMIN
79	USER
79	ADMIN
80	SUBADMIN
80	USER
81	USER
\.


--
-- Data for Name: user_sessions; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.user_sessions (id, active, created_at, device_info, expires_at, ip_address, last_accessed_at, session_id, updated_at, user_id) FROM stdin;
19	f	2025-11-10 05:49:07.194187	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-17 05:49:07.193973	0.0.0.0	2025-11-10 06:01:18.283751	11972ff7-b99e-4c27-abc9-d52c70f771c6	2025-11-10 06:01:18.284419	2
13	f	2025-11-05 16:02:25.804557	Linux armv81 - Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Mobile Safari/537.36	2125-10-12 16:02:25.804341	0.0.0.0	\N	b86ffa9f-53e2-41f4-8d28-3da8a262e1bf	2025-11-05 16:02:25.804565	12
14	f	2025-11-05 16:02:50.282255	Linux armv81 - Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Mobile Safari/537.36	2125-10-12 16:02:50.28199	0.0.0.0	2025-11-05 16:04:06.305049	596bb952-515a-4052-9bf5-8ed59d9d5595	2025-11-05 16:04:06.305864	12
1	f	2025-11-05 10:09:19.688815	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	2125-10-12 10:09:19.688084	0.0.0.0	\N	c1147c80-18ac-4891-8fe4-a268e6ba2104	2025-11-05 10:09:19.688824	1
8	f	2025-11-05 10:49:15.48058	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	2125-10-12 10:49:15.480403	0.0.0.0	2025-11-05 12:43:22.572377	2ff59ccc-dd34-4c9c-a1f3-c79b6a939417	2025-11-05 12:43:22.573402	2
2	f	2025-11-05 10:09:54.289451	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	2125-10-12 10:09:54.289171	0.0.0.0	2025-11-05 10:09:58.618318	1303f0e5-d0cf-4c4c-97b9-49fe1a4a288f	2025-11-05 10:09:58.630553	1
5	f	2025-11-05 10:17:45.268967	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	2125-10-12 10:17:45.268762	0.0.0.0	2025-11-05 10:26:49.788804	8a3f1ea3-634b-40a8-b469-1208add7b15d	2025-11-05 10:26:49.789742	2
16	t	2025-11-07 11:06:32.888853	Android SM-A156E (SDK 35)	2125-10-14 11:06:32.888612	0.0.0.0	2025-12-02 12:29:46.957324	3580e122-5527-4fa9-9d8a-549acaa40ec2	2025-12-02 12:29:46.957798	12
3	f	2025-11-05 10:11:38.345769	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	2125-10-12 10:11:38.345566	0.0.0.0	\N	64747720-54c3-4bf8-8e6d-994cbabcc860	2025-11-05 10:11:38.345772	2
10	f	2025-11-05 12:27:05.599986	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-12 12:27:05.599709	0.0.0.0	2025-11-05 12:29:11.935293	c4f12fe1-e30d-45bc-bdb8-05362944e7a6	2025-11-05 12:29:11.936455	10
9	f	2025-11-05 12:26:26.117705	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-12 12:26:26.117294	0.0.0.0	\N	70adda71-74ac-4b59-bf47-7cc4a3e0fc24	2025-11-05 12:26:26.117714	10
11	f	2025-11-05 12:30:48.030441	Android SM-A146B (SDK 35)	2025-11-12 12:30:48.030127	0.0.0.0	2025-11-12 10:41:01.928598	6c223c85-c5c5-404d-a223-38696846578a	2025-11-12 10:41:01.928995	10
6	t	2025-11-05 10:27:29.202615	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	2125-10-12 10:27:29.202403	0.0.0.0	2025-11-05 11:15:09.14263	964b4782-cb52-4ac9-ab39-5425ed2159e3	2025-11-05 11:15:09.143719	1
20	f	2025-11-10 09:52:05.110683	Linux armv81 - Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-17 09:52:05.110461	0.0.0.0	2025-11-10 09:52:09.929238	89ae4810-9050-4ac8-a0bf-ae5ffb0f3f30	2025-11-10 09:52:09.930164	14
15	f	2025-11-07 06:32:11.859128	Linux armv81 - Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	2125-10-14 06:32:11.85889	0.0.0.0	\N	3d50b7f5-a86d-4d45-9e69-c2964bace1e1	2025-11-07 06:32:11.859137	14
151	t	2025-12-02 05:03:48.459214	Android SM-A146B (SDK 35)	2025-12-09 05:03:48.459006	0.0.0.0	2025-12-04 08:01:48.534459	242dc1a1-4440-4f65-80e3-383151cd2a70	2025-12-04 08:01:48.535339	10
18	f	2025-11-09 06:15:58.654294	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-16 06:15:58.654091	0.0.0.0	2025-11-17 08:39:19.259338	dc84f618-b498-4529-a9f9-fb35cd1f841f	2025-11-17 08:39:19.260112	15
17	f	2025-11-09 06:15:20.830209	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-16 06:15:20.830059	0.0.0.0	\N	340f6286-a79d-4a5c-a36c-9a170d8e9bf2	2025-11-09 06:15:20.830229	15
154	f	2025-12-04 05:41:21.044753	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-11-10 05:41:21.043294	0.0.0.0	\N	d10546e3-d5b8-40a1-b847-2be2952bae33	2025-12-04 05:41:21.044764	68
155	f	2025-12-04 05:42:13.671175	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-11-10 05:42:13.670934	0.0.0.0	2025-12-04 05:42:35.468938	b2205157-5817-40c0-ba85-9c8dee902231	2025-12-04 05:42:35.470047	68
171	f	2025-12-08 06:10:29.7629	Android RMX5110 (SDK 35)	2125-11-14 06:10:29.762634	0.0.0.0	2025-12-08 06:12:48.717491	ea5cb348-3c52-4207-9f34-6d350aeceda1	2025-12-08 06:12:48.718152	70
163	f	2025-12-06 10:05:38.419367	web-client	2125-11-12 10:05:38.419107	0.0.0.0	2025-12-06 10:12:20.577404	46c932e6-583e-4f92-a9b7-b21f4a3ec688	2025-12-06 10:12:20.57809	24
152	f	2025-12-02 17:34:52.265246	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-11-08 17:34:52.265119	0.0.0.0	\N	2550ced6-dc8c-41d8-9024-903736854bb7	2025-12-02 17:34:52.265248	64
30	f	2025-11-11 08:58:08.065198	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-18 08:58:08.065015	0.0.0.0	2025-11-11 08:58:21.838245	c30938b4-f99d-44fb-a53c-c845117b1923	2025-11-11 08:58:21.838602	25
29	f	2025-11-11 08:57:46.606256	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-18 08:57:46.606099	0.0.0.0	\N	6e689a46-c2c2-4d83-a1c9-8f15f88665a9	2025-11-11 08:57:46.606258	25
32	t	2025-11-11 09:02:02.085263	web-client	2025-11-18 09:02:02.085108	0.0.0.0	\N	26f37e9b-1bdc-4620-949e-cf77504d4802	2025-11-11 09:02:02.085266	25
45	f	2025-11-12 11:52:08.440148	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-19 11:52:08.439971	0.0.0.0	2025-11-12 12:02:22.90772	684e3d02-40e5-48e7-84ef-1f6e286a3a9c	2025-11-12 12:02:22.908228	26
48	t	2025-11-13 10:58:21.667203	TECNO TECNO KF6p - 11	2025-11-20 10:58:21.667003	0.0.0.0	2025-11-13 12:39:07.917757	3e280e04-27d1-43cc-a5ae-f9853382408e	2025-11-13 12:39:07.918184	26
39	f	2025-11-11 10:38:37.779976	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-18 10:38:37.779841	0.0.0.0	\N	dd7ca01f-7497-4831-82d0-0c69aa23cd16	2025-11-11 10:38:37.779977	26
35	f	2025-11-11 09:18:53.867189	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-18 09:18:53.867058	0.0.0.0	2025-11-11 09:19:09.957653	a766ab27-35c0-4388-839a-3b8084f84f67	2025-11-11 09:19:09.958055	13
36	f	2025-11-11 09:19:37.952474	web-client	2025-11-18 09:19:37.95232	0.0.0.0	2025-11-11 09:22:35.5106	0fb63273-8cf8-4143-946e-1013dd0ac063	2025-11-11 09:22:35.510943	13
25	t	2025-11-11 06:57:44.183678	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-18 06:57:44.183455	0.0.0.0	2025-11-13 12:05:55.649177	a915cd48-d5b3-4864-b2b3-ff3e402bd10d	2025-11-13 12:05:55.649586	22
27	t	2025-11-11 08:43:30.087195	web-client	2025-11-18 08:43:30.087028	0.0.0.0	2025-11-11 08:58:27.275904	dc005363-9f02-4e2d-b693-96b478d61e02	2025-11-11 08:58:27.276304	22
40	f	2025-11-11 10:38:57.246522	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-18 10:38:57.246351	0.0.0.0	2025-11-11 10:39:09.439659	f80de239-ad3d-472c-9010-b02268ed8bbc	2025-11-11 10:39:09.440076	26
41	f	2025-11-11 10:39:31.266652	web-client	2025-11-18 10:39:31.266487	0.0.0.0	2025-11-11 10:40:26.949565	7e1af137-b0ea-49ee-a11d-625a2162170d	2025-11-11 10:40:26.949994	26
23	f	2025-11-11 06:57:12.891921	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-18 06:57:12.891773	0.0.0.0	\N	c5db2da2-f6a7-40e5-a24f-01263bf19ebf	2025-11-11 06:57:12.891927	22
51	t	2025-11-13 11:20:01.52801	web-client	2025-11-20 11:20:01.527862	0.0.0.0	2025-11-19 12:49:44.975789	5965f051-1768-4a1d-858d-5c4ba814fe43	2025-11-19 12:49:44.97629	26
172	f	2025-12-08 06:13:38.750293	Win32 - Mozilla/5.0 (X11; Linux aarch64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 CrKey/1.54.250320	2125-11-14 06:13:38.750151	0.0.0.0	\N	c741e075-68dc-4c7b-aa70-a00f2f106cb2	2025-12-08 06:13:38.750296	81
28	f	2025-11-11 08:48:57.430344	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-18 08:48:57.430171	0.0.0.0	2025-11-11 09:07:48.929557	f30df16a-8fa1-4644-9d76-38023c675fd0	2025-11-11 09:07:48.93008	2
24	f	2025-11-11 06:57:42.109455	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-18 06:57:42.109218	0.0.0.0	2025-11-11 07:03:52.083944	a532d133-92d9-4c31-83ca-a0a7f70842eb	2025-11-11 07:03:52.084518	23
49	t	2025-11-13 11:10:50.286368	web-client	2025-11-20 11:10:50.286215	0.0.0.0	2025-11-13 11:19:22.712714	eac0efa9-9320-4722-a1bc-fda5a2ad10cf	2025-11-13 11:19:22.713125	26
33	t	2025-11-11 09:10:25.551863	web-client	2025-11-18 09:10:25.551666	0.0.0.0	2025-11-11 09:19:37.947237	9fb3dffb-f238-415e-8346-b7f109a1293d	2025-11-11 09:19:37.947643	22
52	f	2025-11-13 11:50:51.379426	web-client	2125-10-20 11:50:51.379263	0.0.0.0	2025-11-16 04:06:16.26992	919cf5d7-5aa9-4c16-836f-6035f8ff9bc6	2025-11-16 04:06:16.270437	24
37	t	2025-11-11 09:22:35.51586	web-client	2025-11-18 09:22:35.515718	0.0.0.0	2025-11-11 12:39:37.285961	0ca83dac-6943-4b38-a91a-c6a35c23a724	2025-11-11 12:39:37.28638	13
42	f	2025-11-11 10:40:26.954932	web-client	2025-11-18 10:40:26.95481	0.0.0.0	2025-11-11 10:55:38.786613	a64b4be5-1be9-4c83-bb92-68343d05acd3	2025-11-11 10:55:38.78698	26
31	t	2025-11-11 09:00:54.563222	web-client	2025-11-18 09:00:54.563051	0.0.0.0	2025-11-11 09:02:02.078818	0d8a51a5-0d79-4af7-9270-b11e58bc981a	2025-11-11 09:02:02.079339	22
34	t	2025-11-11 09:12:23.22338	web-client	2025-11-18 09:12:23.223211	0.0.0.0	2025-11-11 12:51:38.780004	7517c205-0846-4829-8cf3-2aa99cb9a3e2	2025-11-11 12:51:38.780295	22
156	t	2025-12-04 05:43:03.859829	web-client	2025-12-11 05:43:03.859546	0.0.0.0	2025-12-04 05:49:16.520708	e55d1a0b-9e2b-4fa1-b9e6-4e8bdaddb8ed	2025-12-04 05:49:16.521622	68
53	f	2025-11-13 12:05:12.326983	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-20 12:05:12.326767	0.0.0.0	\N	24ef1fdc-27dc-4ec9-b9d7-9988bc38a64f	2025-11-13 12:05:12.326985	23
54	f	2025-11-13 12:05:15.411833	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-20 12:05:15.411575	0.0.0.0	\N	f6750398-94f7-4af9-99ef-a916431762b6	2025-11-13 12:05:15.411837	23
43	f	2025-11-11 11:01:36.769259	web-client	2025-11-18 11:01:36.769068	0.0.0.0	2025-11-11 12:55:38.870286	44d33e2e-e002-44ae-b825-5415c1564548	2025-11-11 12:55:38.870737	26
44	f	2025-11-12 10:47:46.451811	Android TECNO KF6p (SDK 30)	2025-11-19 10:47:46.451584	0.0.0.0	2025-11-12 11:48:41.479002	8ac96117-7489-487f-8fee-42c5f028a4a5	2025-11-12 11:48:41.479409	26
38	t	2025-11-11 10:33:44.443692	web-client	2025-11-18 10:33:44.443491	0.0.0.0	2025-11-11 10:36:43.115206	8b862871-bd12-479f-8a3b-a76875f101ca	2025-11-11 10:36:43.115855	22
46	f	2025-11-12 13:32:31.410217	NearProp Franchise App	2025-11-19 13:32:31.410077	0.0.0.0	\N	536a9391-2950-48e2-b4be-205e9595fe7b	2025-11-12 13:32:31.410219	26
164	t	2025-12-06 10:12:20.583888	web-client	2125-11-12 10:12:20.583602	0.0.0.0	2025-12-08 05:56:38.523431	0703bc13-7988-46b7-af07-25e4eabcc1f5	2025-12-08 05:56:38.524087	24
47	t	2025-11-13 06:14:49.638337	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-20 06:14:49.638192	0.0.0.0	2025-11-14 07:26:48.805077	90669bbf-952c-4253-81a8-95f8f6d29d08	2025-11-14 07:26:48.805409	26
26	f	2025-11-11 08:22:55.330853	web-client	2125-10-18 08:22:55.330686	0.0.0.0	2025-11-13 06:52:23.744231	e4da3c95-a119-463b-9fc1-79c301cde1d0	2025-11-13 06:52:23.744741	24
4	f	2025-11-05 10:12:42.968145	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	2125-10-12 10:12:42.9679	0.0.0.0	2025-11-05 10:13:29.883459	634d2aff-83e0-4955-897e-986d50009c38	2025-11-05 10:13:29.884394	2
7	f	2025-11-05 10:36:03.084113	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	2125-10-12 10:36:03.083899	0.0.0.0	2025-11-05 10:48:46.349198	a7f6f92f-802c-4df1-8b54-6b828d33430e	2025-11-05 10:48:46.350236	2
173	t	2025-12-08 06:14:06.400868	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-11-14 06:14:06.400712	0.0.0.0	2025-12-08 06:15:17.022183	023584fc-bb51-4ff7-9e0a-e98e4ded7e15	2025-12-08 06:15:17.022639	70
166	t	2025-12-08 03:17:47.008418	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-11-14 03:17:47.008221	0.0.0.0	2025-12-08 06:15:44.73677	50ff1013-f16f-42e7-95a6-38f8d14b4a46	2025-12-08 06:15:44.73717	2
174	t	2025-12-08 06:15:31.616336	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-11-14 06:15:31.616161	0.0.0.0	2025-12-08 06:16:11.214791	effa3e61-e91e-4808-b388-5b5b79a5fa96	2025-12-08 06:16:11.215178	81
165	f	2025-12-08 03:14:05.110605	{{deviceInfo}}	2125-11-14 03:14:05.110427	0.0.0.0	2025-12-08 03:27:26.289777	f98305ed-57bd-412e-8179-bcb683656df4	2025-12-08 03:27:26.290377	78
158	f	2025-12-04 06:03:52.565971	Android RMX5110 (SDK 35)	2125-11-10 06:03:52.565773	0.0.0.0	2025-12-05 08:24:50.230825	5befed24-cd5a-47d3-a922-7268d5787182	2025-12-05 08:24:50.231534	70
55	f	2025-11-13 12:05:16.389464	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-20 12:05:16.389293	0.0.0.0	2025-11-13 12:05:19.859866	08f1b0e2-c32b-4cc3-978e-94a7b4fa7cb7	2025-11-13 12:05:19.860345	23
86	f	2025-11-15 13:52:17.290017	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-22 13:52:17.289875	0.0.0.0	2025-11-15 13:59:22.555465	a138d559-cbc7-423d-8285-cbd5a148f3a1	2025-11-15 13:59:22.555755	2
153	t	2025-12-02 17:35:25.533433	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-11-08 17:35:25.533268	0.0.0.0	2025-12-04 05:39:51.921936	4fd58dd9-5602-4afa-8167-1927053aa4eb	2025-12-04 05:39:51.923314	64
87	f	2025-11-15 16:35:11.855728	Linux armv81 - Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Mobile Safari/537.36	2025-11-22 16:35:11.855578	0.0.0.0	2025-11-15 20:48:54.48376	7b6ed21f-ea83-427b-8972-75a569d3e1dc	2025-11-15 20:48:54.484109	2
157	t	2025-12-04 06:02:24.281169	Android RMX5110 (SDK 35)	2125-11-10 06:02:24.281001	0.0.0.0	2025-12-04 06:02:40.412239	644aad49-13bc-4e21-aa3e-60a2042e2e28	2025-12-04 06:02:40.413349	69
63	f	2025-11-14 10:40:39.626032	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-21 10:40:39.625843	0.0.0.0	2025-11-14 12:25:13.999503	caead1fb-a0ce-4e17-9e59-b41114992804	2025-11-14 12:25:14.000015	23
71	t	2025-11-15 06:25:40.581123	Android TECNO KF6p (SDK 30)	2025-11-22 06:25:40.58098	0.0.0.0	2025-11-17 06:21:11.272464	62caae44-e57d-4e25-8059-cf0ca953ab10	2025-11-17 06:21:11.272899	26
66	t	2025-11-14 13:12:51.755064	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-21 13:12:51.754916	0.0.0.0	2025-11-17 11:26:56.792522	95ee3029-8ecc-4c47-9db3-fa59d4e9a3c2	2025-11-17 11:26:56.793674	26
56	f	2025-11-13 12:11:01.313894	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-20 12:11:01.31371	0.0.0.0	2025-11-13 12:46:25.91022	57cdf7b2-2f27-455f-aaa5-cf04cd518baf	2025-11-13 12:46:25.910826	23
60	f	2025-11-14 08:58:45.251044	samsung SM-A315G - 12	2025-11-21 08:58:45.250859	0.0.0.0	\N	b87fcce7-69f5-487b-b940-95a5d3632dda	2025-11-14 08:58:45.251047	23
61	f	2025-11-14 08:59:53.993294	samsung SM-A315G - 12	2025-11-21 08:59:53.9931	0.0.0.0	\N	852150e2-9bc3-43a4-9828-a1ff16b1ca85	2025-11-14 08:59:53.993298	23
159	t	2025-12-04 08:02:22.718942	Android SM-A146B (SDK 35)	2125-11-10 08:02:22.718597	0.0.0.0	2025-12-05 08:00:15.391392	9ebbfcf1-715c-43a9-8282-79c8ef8609f4	2025-12-05 08:00:15.392001	71
73	t	2025-11-15 07:08:52.517094	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-22 07:08:52.516957	0.0.0.0	2025-11-19 12:19:47.272777	483dddbc-8c36-4a15-bda7-e16529eb8fe5	2025-11-19 12:19:47.273326	26
79	f	2025-11-15 13:22:40.778055	web-client	2025-11-22 13:22:40.777896	0.0.0.0	2025-11-15 13:23:56.532726	2505bd3a-f740-444d-8f41-9485a18a500e	2025-11-15 13:23:56.533191	2
12	f	2025-11-05 12:44:12.510767	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	2025-11-12 12:44:12.510525	0.0.0.0	2025-11-06 07:24:45.364436	f3e2fd0a-c36e-4fc5-ba19-82c1216d248d	2025-11-06 07:24:45.366336	2
77	t	2025-11-15 09:45:46.367638	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-22 09:45:46.367508	0.0.0.0	2025-12-03 06:25:16.890108	924324b0-6bc0-437a-9f6a-e9aa2961de63	2025-12-03 06:25:16.890678	29
57	t	2025-11-13 12:51:42.670394	TECNO TECNO KF6p - 11	2025-11-20 12:51:42.670229	0.0.0.0	2025-11-14 06:01:33.931716	db2bc4a3-53a9-493c-bd18-480e4fc00559	2025-11-14 06:01:33.932083	26
76	f	2025-11-15 09:44:28.996409	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-22 09:44:28.996286	0.0.0.0	\N	05e5d09c-4248-4845-9ca4-a93a743000d6	2025-11-15 09:44:28.996412	29
59	t	2025-11-14 07:31:52.198652	Unknown Device	2025-11-21 07:31:52.198448	0.0.0.0	2025-11-15 07:26:02.555418	4fca3fc9-a288-43b3-a5fc-ab4fe54c353f	2025-11-15 07:26:02.55576	26
58	t	2025-11-14 07:27:24.179056	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-21 07:27:24.178909	0.0.0.0	2025-11-14 07:27:35.801599	bf1c2639-e658-46e1-b25a-d6bef4f99dba	2025-11-14 07:27:35.80188	26
84	f	2025-11-15 13:36:33.377359	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-22 13:36:33.377209	0.0.0.0	2025-11-15 13:39:36.46572	19bbe510-8d20-4296-a451-fb02950a5b3e	2025-11-15 13:39:36.466223	31
50	f	2025-11-13 11:13:37.856358	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-20 11:13:37.856212	0.0.0.0	2025-11-13 12:05:00.544156	94b8ee1f-c87a-4e9e-9bee-6aaca6371994	2025-11-13 12:05:00.544521	2
67	t	2025-11-14 14:19:43.349942	Android TECNO KF6p (SDK 30)	2025-11-21 14:19:43.349797	0.0.0.0	2025-11-15 06:19:21.297627	680b7200-8d6c-43d7-8c11-b351e5ab665e	2025-11-15 06:19:21.297916	26
78	f	2025-11-15 09:55:50.15153	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-22 09:55:50.151395	0.0.0.0	2025-11-15 13:21:41.148074	27c5328f-81ca-41d4-ad31-e4b2489e618f	2025-11-15 13:21:41.14836	30
62	t	2025-11-14 09:11:11.44065	web-client	2025-11-21 09:11:11.440452	0.0.0.0	2025-11-14 13:40:35.896948	d342819c-402c-4c06-acd2-cf6c453059ed	2025-11-14 13:40:35.897317	26
65	t	2025-11-14 13:09:47.213306	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-21 13:09:47.213167	0.0.0.0	2025-11-17 12:42:27.209682	a1fb8773-0f90-40b5-9080-3a956f74807c	2025-11-17 12:42:27.210626	22
22	f	2025-11-11 06:41:21.83247	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-18 06:41:21.83226	0.0.0.0	2025-11-11 06:56:36.033588	96a7e69b-465a-4a82-bdc7-5b1c1989f16c	2025-11-11 06:56:36.034255	2
64	f	2025-11-14 11:12:32.489851	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-21 11:12:32.489703	0.0.0.0	2025-11-14 11:13:14.030728	7c4c29e1-7f8c-4b8b-b1c5-b88bcb6a145a	2025-11-14 11:13:14.031156	2
80	f	2025-11-15 13:23:56.537152	web-client	2025-11-22 13:23:56.537022	0.0.0.0	2025-11-15 13:29:03.891568	8c997f38-f5e9-4c28-9ad6-1b74cfd73093	2025-11-15 13:29:03.891865	30
167	f	2025-12-08 03:29:08.430827	{{deviceInfo}}	2125-11-14 03:29:08.430666	0.0.0.0	2025-12-08 03:34:37.469613	41021674-579b-4a4a-b1c1-5c3fddc5820b	2025-12-08 03:34:37.470203	79
75	t	2025-11-15 08:40:44.130571	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-22 08:40:44.130429	0.0.0.0	2025-11-15 09:56:28.455236	64a22d1c-473d-4c09-94e9-d69afc36ed70	2025-11-15 09:56:28.455618	28
74	f	2025-11-15 08:40:01.54975	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-22 08:40:01.549594	0.0.0.0	\N	a3a5aa40-8f7e-42f8-b138-4dc7fc4350f6	2025-11-15 08:40:01.549752	28
82	f	2025-11-15 13:32:29.452148	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-22 13:32:29.452023	0.0.0.0	2025-11-15 13:32:51.162903	be60198a-40ea-4961-9a01-a35ee074575d	2025-11-15 13:32:51.16317	31
81	f	2025-11-15 13:31:59.4328	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-22 13:31:59.432682	0.0.0.0	\N	5f7cd618-2a10-46d7-ba23-3ee92a37319c	2025-11-15 13:31:59.432802	31
83	f	2025-11-15 13:33:33.609761	web-client	2025-11-22 13:33:33.609577	0.0.0.0	2025-11-15 13:35:59.13825	bfbf1b4b-5d9f-4ab4-b69c-fa54abca297d	2025-11-15 13:35:59.138683	31
102	f	2025-11-17 07:05:20.454911	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-24 07:05:20.454529	0.0.0.0	\N	739b31f0-b67b-44fe-9191-1be6c357d6c7	2025-11-17 07:05:20.45492	36
92	t	2025-11-16 04:20:06.106548	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0	2025-11-23 04:20:06.106395	0.0.0.0	2025-11-16 04:21:04.254244	a85f23a7-84f7-449e-a772-aab645d722f1	2025-11-16 04:21:04.254531	30
91	t	2025-11-16 04:17:36.197312	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-23 04:17:36.197161	0.0.0.0	2025-11-16 04:27:24.325061	7e380213-730c-4e5c-a5d6-fd5c57fcb3e5	2025-11-16 04:27:24.325328	32
93	f	2025-11-16 04:28:34.363708	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-23 04:28:34.363591	0.0.0.0	\N	0ffdd129-e6ce-4557-befd-17f7bc7c7a9d	2025-11-16 04:28:34.36371	33
103	t	2025-11-17 07:06:02.877128	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-24 07:06:02.876834	0.0.0.0	2025-11-24 11:26:01.819609	aed8cae0-636d-4de4-8dd5-87f630bcc9a1	2025-11-24 11:26:01.820176	36
90	f	2025-11-16 04:08:32.390748	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-23 04:08:32.390619	0.0.0.0	2025-11-16 04:18:04.135059	8f92978e-c503-477d-bee2-dcb1d9517a91	2025-11-16 04:18:04.13541	30
112	t	2025-11-17 10:52:33.721038	Android TECNO KF6p (SDK 30)	2025-11-24 10:52:33.720878	0.0.0.0	2025-11-17 11:13:00.336312	c0000b29-9974-4b52-8cc2-6477050a26e7	2025-11-17 11:13:00.338096	26
108	t	2025-11-17 10:19:05.75279	Android TECNO KF6p (SDK 30)	2025-11-24 10:19:05.752606	0.0.0.0	2025-11-17 10:19:41.29763	93ef9d7a-63fb-45f7-81ef-c29b5e942ea8	2025-11-17 10:19:41.298278	26
110	f	2025-11-17 10:40:35.569088	Android SM-A146B (SDK 35)	2025-11-24 10:40:35.568877	0.0.0.0	2025-11-22 18:12:54.706357	364a3abd-4b70-4418-8345-e125c869b0eb	2025-11-22 18:12:54.706761	10
89	f	2025-11-16 04:06:54.591692	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-23 04:06:54.591557	0.0.0.0	2025-11-16 04:08:08.512839	1c8ad403-dfd2-4d62-94c9-4fa242150ab8	2025-11-16 04:08:08.513165	2
94	t	2025-11-16 04:28:53.145577	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-23 04:28:53.145429	0.0.0.0	2025-11-16 08:59:06.386944	1557fb23-8afc-4cea-b05a-eada7e1f32a2	2025-11-16 08:59:06.387275	33
85	t	2025-11-15 13:40:12.752677	web-client	2025-11-22 13:40:12.752528	0.0.0.0	2025-11-16 04:34:29.302561	6cfeb769-848c-44f8-a067-12cf9e9fc2e3	2025-11-16 04:34:29.303032	31
168	f	2025-12-08 05:32:20.26713	{{deviceInfo}}	2125-11-14 05:32:20.266911	0.0.0.0	2025-12-08 05:51:00.007712	7a7d6e81-9c1f-4078-b6b7-35bd2c9a3066	2025-12-08 05:51:00.008305	78
95	f	2025-11-16 04:32:58.991441	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-23 04:32:58.99133	0.0.0.0	\N	c3ef4fba-6c96-4173-8255-e64fa0bb8624	2025-11-16 04:32:58.991442	34
96	f	2025-11-16 04:33:48.174445	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-23 04:33:48.174304	0.0.0.0	2025-11-16 04:34:13.92693	7506427b-a34d-4e7c-b472-667eca87647c	2025-11-16 04:34:13.927186	34
109	t	2025-11-17 10:32:28.798557	Android TECNO KF6p (SDK 30)	2025-11-24 10:32:28.798354	0.0.0.0	2025-11-17 10:45:55.735023	2fe0027d-ae16-4dac-b764-711aba4d75c5	2025-11-17 10:45:55.735562	26
118	t	2025-11-17 12:18:48.510903	Android SM-E156B (SDK 35)	2125-10-24 12:18:48.510609	0.0.0.0	2025-12-05 16:57:15.290907	c25c0160-cab0-4ba7-af49-9de80428a911	2025-12-05 16:57:15.291758	14
106	f	2025-11-17 09:25:55.017988	Linux armv81 - Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Mobile Safari/537.36	2125-10-24 09:25:55.017806	0.0.0.0	2025-11-17 09:25:59.956187	0a145be6-0d84-43fd-9a2e-eb78f2c4863f	2025-11-17 09:25:59.956955	38
105	f	2025-11-17 09:25:37.511877	Linux armv81 - Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Mobile Safari/537.36	2125-10-24 09:25:37.511711	0.0.0.0	\N	0e4a45de-c919-4f8e-9cb9-d270678afb69	2025-11-17 09:25:37.511882	38
99	t	2025-11-16 08:58:39.915801	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-23 08:58:39.915666	0.0.0.0	2025-11-17 07:06:34.813579	456894b1-53d7-4512-abdb-936d4ce2724d	2025-11-17 07:06:34.814777	35
100	f	2025-11-16 18:42:22.077139	Android SM-A146B (SDK 35)	2025-11-23 18:42:22.077016	0.0.0.0	2025-11-17 08:30:41.620309	026be007-207e-494a-ac04-7e0b07af74db	2025-11-17 08:30:41.621196	10
107	t	2025-11-17 09:26:32.387709	Android SM-A366E (SDK 36)	2125-10-24 09:26:32.387489	0.0.0.0	2025-12-02 04:06:33.623588	e4097650-a602-4af0-9cee-a63f4e8ce247	2025-12-02 04:06:33.624071	38
101	t	2025-11-17 06:58:44.316148	Android TECNO KF6p (SDK 30)	2025-11-24 06:58:44.31529	0.0.0.0	2025-11-17 10:12:48.726523	2d27a4bd-dd13-47da-b319-8f81e738b5d1	2025-11-17 10:12:48.727239	26
97	t	2025-11-16 04:35:05.197905	web-client	2025-11-23 04:35:05.197789	0.0.0.0	2025-11-16 04:38:06.398362	ce28024c-acb9-4033-8f0f-316cae732a12	2025-11-16 04:38:06.398776	34
98	f	2025-11-16 08:57:56.990979	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-23 08:57:56.990858	0.0.0.0	\N	2fe522fc-7435-497d-b9ec-f9df047ae6db	2025-11-16 08:57:56.990981	35
116	f	2025-11-17 12:00:46.567003	web-client	2125-10-24 12:00:46.566458	0.0.0.0	2025-11-17 12:00:46.962665	fab1f117-14f9-435a-be99-9200348672e7	2025-11-17 12:00:46.963719	24
160	t	2025-12-04 12:33:26.399092	Android SM-E156B (SDK 36)	2025-12-11 12:33:26.398858	0.0.0.0	2025-12-04 12:36:03.075305	43a6b2b3-8447-4336-a032-278f7441c2ef	2025-12-04 12:36:03.076046	15
114	f	2025-11-17 11:30:01.882044	web-client	2125-10-24 11:30:01.881663	0.0.0.0	2025-11-17 12:00:46.55822	39a905e8-b1b9-4ca1-8fdf-6bf9f422521d	2025-11-17 12:00:46.559244	24
111	f	2025-11-17 10:50:19.072855	samsung SM-A146B - 15	2125-10-24 10:50:19.072615	0.0.0.0	\N	be903e88-f8ae-4360-95fd-7a7bbef94f4b	2025-11-17 10:50:19.07286	39
117	f	2025-11-17 12:02:23.560234	web-client	2125-10-24 12:02:23.559994	0.0.0.0	2025-11-17 12:04:41.078709	5ecc9c00-89a4-4996-baf0-78521e0b8aa3	2025-11-17 12:04:41.079735	24
119	t	2025-11-17 12:33:39.419415	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-24 12:33:39.419225	0.0.0.0	\N	0a8469e1-9911-4339-aca2-dd4b0cfbb924	2025-11-17 12:33:39.419421	49
21	f	2025-11-11 06:01:54.169764	Linux armv81 - Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Mobile Safari/537.36	2025-11-18 06:01:54.169507	0.0.0.0	2025-11-11 06:02:14.451127	7427980e-1cb3-4231-ba50-2fb54c79c43f	2025-11-11 06:02:14.451776	2
113	f	2025-11-17 11:28:09.824738	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-24 11:28:09.824305	0.0.0.0	\N	0098348d-b1be-4411-940f-a056fd950aa5	2025-11-17 11:28:09.824749	41
115	f	2025-11-17 11:32:26.967333	Android TECNO KF6p (SDK 30)	2125-10-24 11:32:26.966883	0.0.0.0	2025-11-17 11:33:51.001295	b46d8c8a-1264-48f0-ae94-4ec325857f8a	2025-11-17 11:33:51.002354	41
120	t	2025-11-17 12:42:12.936289	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2125-10-24 12:42:12.935927	0.0.0.0	2025-11-20 13:01:21.102875	1decb56a-1f74-4d2c-a414-841247f35d5c	2025-11-20 13:01:21.103289	41
144	f	2025-11-25 11:17:39.049732	samsung SM-A146B - 15	2025-12-02 11:17:39.04955	0.0.0.0	\N	9527f1ec-a57b-4845-bd39-1abd0dac4b7b	2025-11-25 11:17:39.049736	10
146	t	2025-11-25 11:23:10.849312	realme RMX5110 - 15	2025-12-02 11:23:10.849165	0.0.0.0	2025-11-25 11:23:16.6055	995f945e-2d1d-464b-97a6-c75faebd0b54	2025-11-25 11:23:16.605967	26
147	t	2025-11-25 11:24:24.389395	realme RMX5110 - 15	2025-12-02 11:24:24.389249	0.0.0.0	2025-11-25 11:24:25.017182	924b682b-686e-4846-9591-8af51ef55359	2025-11-25 11:24:25.01761	26
121	t	2025-11-17 12:42:46.004239	Android TECNO KF6p (SDK 30)	2025-11-24 12:42:46.004007	0.0.0.0	2025-11-17 12:45:26.013631	6aeac1d1-17d6-4ca1-9341-107f20957238	2025-11-17 12:45:26.014882	26
104	f	2025-11-17 09:18:39.250809	Android SM-E156B (SDK 35)	2025-11-24 09:18:39.250599	0.0.0.0	2025-11-22 13:12:45.039689	4658c08a-beb1-445f-9447-1a06b2e46952	2025-11-22 13:12:45.040123	15
127	t	2025-11-18 07:33:09.077691	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-25 07:33:09.077508	0.0.0.0	2025-11-18 10:16:37.558313	5646baee-f542-4fb8-aab7-1622266c2718	2025-11-18 10:16:37.558774	26
129	f	2025-11-18 07:49:27.939065	samsung SM-A146B - 15	2125-10-25 07:49:27.938886	0.0.0.0	\N	d97c8335-5d45-467f-ad74-b4c8b6e585a9	2025-11-18 07:49:27.939075	39
131	t	2025-11-18 10:02:24.929606	web-client	2125-10-25 10:02:24.929419	0.0.0.0	2025-11-18 10:03:07.430024	1ca8a641-3a82-4046-9377-2a44af724de8	2025-11-18 10:03:07.430684	39
68	f	2025-11-15 04:45:52.266855	motorola moto g82 5G - 13	2025-11-22 04:45:52.266669	0.0.0.0	\N	fce2b350-f468-46af-8c9f-fb223e0c3bd6	2025-11-15 04:45:52.266858	23
69	f	2025-11-15 04:46:50.213324	motorola moto g82 5G - 13	2025-11-22 04:46:50.213127	0.0.0.0	\N	760d44e4-d695-4fd9-89c9-bc2dd86f3ccc	2025-11-15 04:46:50.213327	23
125	t	2025-11-18 06:32:00.339019	Android TECNO KF6p (SDK 30)	2125-10-25 06:32:00.338831	0.0.0.0	2025-11-18 07:11:08.318141	79b2b52b-a725-4982-98ce-b5b94ff3b75c	2025-11-18 07:11:08.318747	53
70	f	2025-11-15 04:47:02.082857	motorola moto g82 5G - 13	2025-11-22 04:47:02.082691	0.0.0.0	\N	19b8612b-f1f0-4dad-8386-ccb516cd7040	2025-11-15 04:47:02.08286	23
126	t	2025-11-18 06:36:07.127881	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-25 06:36:07.127692	0.0.0.0	2025-11-18 07:32:33.452833	dafbd961-0969-4c82-b987-e26adf953b9e	2025-11-18 07:32:33.453391	26
72	f	2025-11-15 06:52:42.600017	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-11-22 06:52:42.599843	0.0.0.0	2025-11-15 08:41:16.193704	612d7c99-78d4-4c5c-86f2-02bc04bc6478	2025-11-15 08:41:16.194057	23
137	f	2025-11-19 11:03:24.864499	web-client	2125-10-26 11:03:24.864316	0.0.0.0	2025-11-19 12:17:24.600977	e83cb617-b4d5-489e-9e75-f271742d8ef6	2025-11-19 12:17:24.601569	54
142	t	2025-11-20 07:25:54.571916	web-client	2025-11-27 07:25:54.571703	0.0.0.0	2025-11-20 13:05:56.332371	9fc9cecc-27cf-40f2-a492-79df4633d9d5	2025-11-20 13:05:56.332922	22
130	f	2025-11-18 09:04:55.015118	samsung SM-A146B - 15	2125-10-25 09:04:55.014951	0.0.0.0	\N	f70dcfe1-bb73-40dc-b343-3ca4df5bdb8f	2025-11-18 09:04:55.01513	54
132	f	2025-11-18 10:03:07.435731	web-client	2125-10-25 10:03:07.4356	0.0.0.0	2025-11-18 10:03:08.308513	ece7bc08-d8ad-4461-9cce-08a302326ec9	2025-11-18 10:03:08.309071	54
135	f	2025-11-19 09:44:38.246236	web-client	2125-10-26 09:44:38.246045	0.0.0.0	2025-11-19 11:02:29.158894	887b9617-924e-4004-ac41-712539b07ce2	2025-11-19 11:02:29.159508	54
139	f	2025-11-19 12:20:23.058738	web-client	2125-10-26 12:20:23.058576	0.0.0.0	2025-11-22 08:10:40.530059	0c4349d6-9df7-4d54-b6fd-f09b1c85f111	2025-11-22 08:10:40.530504	54
161	t	2025-12-05 08:25:23.031628	Android RMX5110 (SDK 35)	2025-12-12 08:25:23.031367	0.0.0.0	2025-12-08 06:09:02.347222	646df95c-920b-4004-9a05-9180cc9c093b	2025-12-08 06:09:02.34775	26
128	t	2025-11-18 07:33:53.050494	Android TECNO KF6p (SDK 30)	2025-11-25 07:33:53.050304	0.0.0.0	2025-11-18 10:34:32.027017	bf797edd-da6b-43f2-9a34-9c24e49404e9	2025-11-18 10:34:32.027582	26
138	t	2025-11-19 12:10:12.681733	TECNO TECNO KF6p - 11	2025-11-26 12:10:12.681515	0.0.0.0	2025-11-19 12:10:15.182535	fc299fd6-02bf-4ad4-9433-2a50087ce0bb	2025-11-19 12:10:15.183116	26
145	t	2025-11-25 11:19:52.716029	samsung SM-A146B - 15	2125-11-01 11:19:52.715898	0.0.0.0	\N	867bb05e-f9ae-42eb-9bb3-15df9808510e	2025-11-25 11:19:52.716032	54
169	t	2025-12-08 05:56:38.530259	web-client	2125-11-14 05:56:38.530069	0.0.0.0	2025-12-08 06:21:52.266052	72ff7cfb-b0a3-4832-814d-c97065b34fb8	2025-12-08 06:21:52.266463	78
143	f	2025-11-22 08:13:43.416532	web-client	2125-10-29 08:13:43.416357	0.0.0.0	2025-11-22 08:14:03.607217	8407d71e-a473-4494-bab5-5c2f725300dd	2025-11-22 08:14:03.607683	24
88	f	2025-11-16 04:06:16.275352	web-client	2125-10-23 04:06:16.275208	0.0.0.0	2025-11-17 11:30:01.865259	7160df56-fbee-4d60-b676-315ed986f840	2025-11-17 11:30:01.867048	24
136	f	2025-11-19 10:51:24.163933	web-client	2125-10-26 10:51:24.163699	0.0.0.0	2025-11-19 12:19:27.552421	434601d6-2e4f-4f6f-b2a3-6cf4d414fd59	2025-11-19 12:19:27.552952	24
141	f	2025-11-19 12:31:14.767519	web-client	2125-10-26 12:31:14.767324	0.0.0.0	2025-11-22 07:25:34.897933	ea572ea1-1433-4822-8a24-d3e0f0e0397c	2025-11-22 07:25:34.898509	24
122	f	2025-11-17 12:54:46.677111	samsung SM-A566B - 16	2025-11-24 12:54:46.676844	0.0.0.0	\N	3432c2f3-d7ba-436f-baf8-da40f1045b66	2025-11-17 12:54:46.677121	23
148	f	2025-11-25 13:34:03.42857	Android RMX5110 (SDK 35)	2025-12-02 13:34:03.428342	0.0.0.0	2025-11-25 13:34:20.2357	4ee37893-c0c1-4358-9730-1299ce5b2f2c	2025-11-25 13:34:20.23606	23
123	f	2025-11-17 12:55:17.28481	samsung SM-A566B - 16	2025-11-24 12:55:17.284557	0.0.0.0	\N	a6615583-2cd6-452d-92b4-7867a225c425	2025-11-17 12:55:17.284819	23
133	f	2025-11-19 05:18:59.260027	google Pixel 4a - 11	2025-11-26 05:18:59.259833	0.0.0.0	\N	f9afb106-33b4-4b92-954f-ae3e12511e0e	2025-11-19 05:18:59.260032	23
134	f	2025-11-19 05:19:23.867507	google Pixel 4a - 11	2025-11-26 05:19:23.867284	0.0.0.0	\N	540e1238-b7f5-439f-9a4a-ffc2c14d0351	2025-11-19 05:19:23.867513	23
140	f	2025-11-19 12:21:42.797104	TECNO TECNO KF6p - 11	2025-11-26 12:21:42.79693	0.0.0.0	\N	b61c415d-2dcf-49ba-9d07-c58c14272790	2025-11-19 12:21:42.797108	23
149	f	2025-11-25 13:35:15.559568	realme RMX5110 - 15	2025-12-02 13:35:15.559374	0.0.0.0	\N	3f0d7bfa-f4a5-47a4-8979-02c6c71f5aee	2025-11-25 13:35:15.559573	23
150	t	2025-11-25 13:36:03.565454	Win32 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36	2025-12-02 13:36:03.565177	0.0.0.0	2025-11-25 13:37:24.301837	ddb38a6e-bf78-49d6-bebf-0c0e77e5101e	2025-11-25 13:37:24.30218	23
162	f	2025-12-06 06:17:04.510039	web-client	2125-11-12 06:17:04.509766	0.0.0.0	2025-12-06 08:28:26.623033	b49433d9-37c9-4179-bcb5-2a00ba9e7a8a	2025-12-06 08:28:26.623942	24
170	t	2025-12-08 05:57:50.494735	{{deviceInfo}}	2125-11-14 05:57:50.494578	0.0.0.0	2025-12-08 05:58:46.596297	1eeeb703-24d1-480b-b896-231b2f56d224	2025-12-08 05:58:46.597042	79
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: nearprop_user
--

COPY public.users (id, aadhaar_document_url, aadhaar_number, aadhaar_verified, address, created_at, district, district_id, email, email_verified, last_login_at, latitude, longitude, mobile_number, mobile_verified, name, password, permanent_id, profile_image_url, updated_at) FROM stdin;
26	\N	\N	f	\N	2025-11-11 10:38:21.547848	\N	332	abhishekh@acoreithub.com	f	\N	22.7495064	75.8992733	+919644782290	t	Abhishek Kachhawa	\N	RANPU2025111110382110	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/profiles/26/abhishek-kachhawa_profile_pic.png	2025-12-05 08:25:27.659136
2	\N	\N	f	\N	2025-11-05 10:11:24.592826	\N	332	shahnawazacore@gmail.com	f	\N	22.7344384	75.890688	+919171617595	t	shahnawaz	\N	RANPU202511051011242	\N	2025-11-11 08:49:20.203321
25	\N	\N	f	\N	2025-11-11 08:57:28.200938	\N	332	\N	f	\N	22.7344384	75.890688	+918839093270	t	bhushan	\N	RANPU202511110857289	\N	2025-11-11 09:06:55.014205
11	\N	\N	f	\N	2025-11-05 15:57:01.498838	\N	\N	\N	f	\N	\N	\N	7979970105	f	Priyanshu	\N	RANPU202511051557012	\N	2025-11-05 15:57:01.498851
47	\N	\N	f	\N	2025-11-17 12:25:39.15145	\N	\N	\N	f	\N	\N	\N	9644782298	f	Abhimanyu gs	\N	RANPU202511171225395	\N	2025-11-17 12:25:39.151461
48	\N	\N	f	\N	2025-11-17 12:26:36.287831	\N	\N	\N	f	\N	\N	\N	+919644782298	f	userr	\N	RANPU202511171226366	\N	2025-11-17 12:26:36.287843
28	\N	\N	f	\N	2025-11-15 08:38:45.156186	\N	\N	\N	f	\N	\N	\N	+916265536685	t	Lomash	\N	RANPU2025111508384511	\N	2025-11-15 08:40:01.548025
49	\N	\N	f	\N	2025-11-17 12:30:56.084842	\N	\N	\N	f	\N	\N	\N	+919893784776	t	asdfgjkl	\N	RANPU202511171230567	\N	2025-11-17 12:33:39.417278
38	\N	\N	f	\N	2025-11-17 09:25:27.874469	\N	75	\N	f	\N	25.411543	86.1155376	+919031638022	t	Abhishek Singh	\N	RANPU202511170925273	\N	2025-11-20 06:25:22.102816
17	\N	\N	f	\N	2025-11-10 07:07:50.624151	\N	\N	\N	f	\N	\N	\N	9279701351	f	Abhishek Kumar Singh	\N	RANPU202511100707507	\N	2025-11-10 07:07:50.624161
36	\N	\N	f	\N	2025-11-17 07:05:10.005372	\N	\N	\N	f	\N	\N	\N	+917223917158	t	Vinayak	\N	RANPU202511170705101	\N	2025-11-17 07:05:20.447782
54	\N	\N	f	NearProp Admin	2025-11-18 07:52:26.912273	Indore	\N	admin@gmail.com	t	\N	\N	\N	9155105666	t	Admin 	$2a$10$cwnxZnb8KFrjxIYGr.4yruQdyQG9qGUljqDV5j4fPsN.uLGTrhUri	\N	\N	2025-11-18 07:52:26.912308
24	\N	\N	f	NearProp Admin	2025-11-11 08:22:34.999053	Indore	\N	Rohitkiaaan@gmail.com	t	\N	\N	\N	6265861847	t	Rohit Patel	$2a$10$9pm94SgpNZYZCNK06K2Zv.XP7E7Bt/Sngm7wGMV0TeEeS3SNmwIA2	\N	\N	2025-11-11 08:22:34.999061
62	\N	\N	f	\N	2025-12-02 04:27:26.828927	\N	\N	\N	f	\N	\N	\N	919263250350	f	kislay	\N	RANPU2025120204272614	\N	2025-12-02 04:27:26.82893
29	\N	\N	f	\N	2025-11-15 09:42:49.469551	\N	\N	\N	f	\N	\N	\N	+917748076274	t	Lomash Badole	\N	RANPU2025111509424912	\N	2025-11-15 09:44:28.994896
13	\N	\N	f	\N	2025-11-06 11:42:10.57318	\N	332	\N	f	\N	22.7344384	75.890688	+917440738681	t	Aditi Tarani	\N	RANPU202511061142104	\N	2025-11-11 12:39:37.216621
50	\N	\N	f	\N	2025-11-17 12:45:53.657222	\N	\N	\N	f	\N	\N	\N	9897847758	f	Abhimanyu Kumawat and	\N	RANPU202511171245538	\N	2025-11-17 12:45:53.657233
30	\N	\N	f	\N	2025-11-15 09:49:30.210941	\N	\N	\N	f	\N	\N	\N	+918718936826	t	Jenab 	\N	RANPU2025111509493013	\N	2025-11-15 09:55:50.149627
22	\N	\N	f	\N	2025-11-11 06:56:57.73339	\N	332	\N	f	\N	22.7435709	75.8708128	+918889084453	t	arpita	\N	RANPU202511110656578	\N	2025-11-20 07:25:56.469284
41	\N	\N	f	\N	2025-11-17 11:27:34.66384	\N	332	\N	f	\N	22.7435709	75.8708128	+919301115547	t	Chirag Sir	\N	RANPU202511171127341	\N	2025-11-20 07:28:06.166927
39	\N	\N	f	\N	2025-11-17 10:24:12.616043	\N	\N	\N	f	\N	\N	\N	6203321252	t	Nirmal	\N	RANPU202511171024124	\N	2025-11-17 10:50:19.071168
31	\N	\N	f	\N	2025-11-15 13:31:49.686364	\N	\N	\N	f	\N	\N	\N	+918839197575	t	Hunen	\N	RANPU2025111513314914	\N	2025-11-15 13:31:59.431247
63	\N	\N	f	\N	2025-12-02 06:13:02.536927	\N	\N	\N	f	\N	\N	\N	8409777644	f	aman Kumar	\N	RANPU2025120206130215	\N	2025-12-02 06:13:02.536931
12	\N	\N	f	\N	2025-11-05 16:02:07.170295	\N	75	\N	f	\N	25.4126684	86.1210282	+917979970105	t	Priyanshu 	\N	RANPU202511051602073	\N	2025-12-02 12:29:03.166465
23	\N	\N	f	\N	2025-11-11 06:57:36.60762	\N	332	\N	t	\N	22.7495141	75.8993	+919012345678	t	Dummy User	\N	DUMMY-1762844256607	\N	2025-11-25 13:40:01.780993
32	\N	\N	f	\N	2025-11-16 04:16:58.049997	\N	\N	\N	f	\N	\N	\N	+918349277362	t	Dipti	\N	RANPU2025111604165815	\N	2025-11-16 04:17:36.195029
33	\N	\N	f	\N	2025-11-16 04:28:24.485957	\N	\N	\N	f	\N	\N	\N	+917089146366	t	Amaan	\N	RANPU2025111604282416	\N	2025-11-16 04:28:34.362299
34	\N	\N	f	\N	2025-11-16 04:32:46.967885	\N	\N	\N	f	\N	\N	\N	+919424786425	t	Soman 	\N	RANPU2025111604324617	\N	2025-11-16 04:32:58.990014
35	\N	\N	f	\N	2025-11-16 08:57:49.098438	\N	\N	\N	f	\N	\N	\N	+917805978708	t	Arsalan	\N	RANPU2025111608574918	\N	2025-11-16 08:57:56.989325
40	\N	\N	f	\N	2025-11-17 11:14:36.427523	\N	\N	\N	f	\N	\N	\N	9301115547	f	Chirag sir	\N	RANPU202511171114361	\N	2025-11-17 11:14:36.42754
51	\N	\N	f	\N	2025-11-17 12:59:04.625614	\N	\N	\N	f	\N	\N	\N	9897847752	f	Abhimanyu Kumawat and	\N	RANPU202511171259049	\N	2025-11-17 12:59:04.625626
56	\N	\N	f	\N	2025-11-21 04:37:26.991704	\N	\N	\N	f	\N	\N	\N	7050964323	f	Bobby	\N	RANPU2025112104372612	\N	2025-11-21 04:37:26.991709
42	\N	\N	f	\N	2025-11-17 11:34:32.33391	\N	\N	\N	f	\N	\N	\N	62655366885	f	lomesh	\N	RANPU202511171134322	\N	2025-11-17 11:34:32.333922
43	\N	\N	f	\N	2025-11-17 11:42:46.88025	\N	\N	\N	f	\N	\N	\N	6265536685	f	lomesh	\N	RANPU202511171142463	\N	2025-11-17 11:42:46.880263
46	\N	\N	f	\N	2025-11-17 12:22:47.543802	\N	\N	\N	f	\N	\N	\N	9893488567	f	Radhe	\N	RANPU202511171222474	\N	2025-11-17 12:22:47.543817
71	\N	\N	f	\N	2025-12-04 08:02:06.263345	\N	75	\N	f	\N	25.4116356	86.115482	+919155105666	t	raja	\N	RANPU202512040802064	\N	2025-12-05 07:58:24.265652
69	\N	\N	f	\N	2025-12-04 06:02:02.732748	\N	332	\N	f	\N	22.7495127	75.8993093	+918821991572	t	hshhs	\N	RANPU202512040602022	\N	2025-12-04 06:02:30.002324
14	\N	\N	f	\N	2025-11-07 06:31:54.014083	\N	75	\N	f	\N	25.5343767	86.0920306	+918809105666	t	Jaideo Kumar	\N	RANPU202511070631545	\N	2025-11-27 07:18:25.815139
53	\N	\N	f	\N	2025-11-18 06:31:46.780366	\N	332	\N	f	\N	22.7495081	75.8993279	+919893784778	t	Abhishek Kachhawa	\N	RANPU2025111806314611	\N	2025-11-18 07:10:39.031616
37	\N	\N	f	\N	2025-11-17 09:24:11.696959	\N	75	\N	f	\N	25.4114089	86.1157107	9031638022	f	Abhishek Kumar Singh	\N	RANPU202511170924112	\N	2025-11-29 11:49:34.099894
64	\N	\N	f	\N	2025-12-02 17:34:35.462347	\N	\N	\N	f	\N	\N	\N	+919977855379	t	abc	\N	RANPU2025120217343516	\N	2025-12-02 17:34:52.263332
61	\N	\N	f	\N	2025-12-02 04:24:57.949602	\N	\N	\N	f	\N	\N	\N	9263250350	f	kislay	\N	RANPU2025120204245713	\N	2025-12-02 04:24:57.949606
65	\N	\N	f	\N	2025-12-02 18:26:33.976436	\N	\N	\N	f	\N	\N	\N	9755579700	f	kp	\N	RANPU2025120218263317	\N	2025-12-02 18:26:33.976441
1	\N	\N	f	\N	2025-11-05 10:08:52.587973	\N	\N	undefined	f	\N	\N	\N	+919399377862	t	Trapti Shukla	\N	RANPU202511051008521	\N	2025-12-03 10:54:27.589291
66	\N	\N	f	\N	2025-12-03 11:08:30.506813	\N	\N	\N	f	\N	\N	\N	9955103005	f	kislay	\N	RANPU2025120311083018	\N	2025-12-03 11:08:30.506817
67	\N	\N	f	\N	2025-12-03 11:09:17.373229	\N	\N	\N	f	\N	\N	\N	9102209454	f	kislay	\N	RANPU2025120311091719	\N	2025-12-03 11:09:17.373232
68	\N	\N	f	\N	2025-12-04 05:41:11.980744	\N	\N	\N	f	\N	\N	\N	+918351927365	t	Abhishek	\N	RANPU202512040541111	\N	2025-12-04 05:41:21.034547
10	\N	\N	f	\N	2025-11-05 12:25:42.900923	\N	75	\N	f	\N	25.4116316	86.1154615	+916203321252	t	NIRMAL KUMAR	\N	RANPU202511051225421	\N	2025-12-04 06:16:53.348293
15	\N	\N	f	\N	2025-11-09 06:14:49.787023	\N	75	\N	f	\N	25.4115869	86.1154739	+919065105666	t	Vikram	\N	RANPU202511090614496	\N	2025-12-04 12:33:30.073072
76	\N	\N	f	\N	2025-12-06 07:04:30.177384	\N	\N	kaifadasd@gmail.com	f	\N	\N	\N	9977855344	f	Kaif	password123	\N	\N	\N
77	\N	\N	f	Ujjain square	2025-12-06 07:09:18.600608	Indore	\N	KJFGH@gmail.com	t	\N	\N	\N	9971855340	t	Kaif	\N	\N	\N	2025-12-06 07:09:18.600623
78	\N	\N	f	Ujjain square	2025-12-06 07:10:38.012931	Indore	\N	hunenkhan@gmail.com	t	\N	\N	\N	8839197575	t	Hunen khan	\N	\N	\N	2025-12-06 07:10:38.012939
79	\N	\N	f	\N	2025-12-08 03:28:40.890914	\N	\N	srk.acore13@gmail.com	f	\N	\N	\N	9171617595	t	Tese For Property	\N	RANPU202512080328405	\N	2025-12-08 03:29:08.429066
80	\N	\N	f	ujjain	2025-12-08 06:08:22.127182	ujjain	\N	ummesulem@gmail.com	t	\N	\N	\N	9926757865	t	Umme Sulem	\N	\N	\N	2025-12-08 06:08:22.127187
70	\N	\N	f	\N	2025-12-04 06:03:31.898435	\N	332	abhishekkachhawa1205@gmail.com	f	\N	22.7495552	75.8993125	+917049433520	t	Suman Rathore	\N	RANPU202512040603313	https://my-nearprop-bucket.s3.ap-south-1.amazonaws.com/profiles/70/suman-rathore_profile_pic.jpg	2025-12-08 06:11:48.929778
81	\N	\N	f	\N	2025-12-08 06:13:05.781419	\N	\N	\N	f	\N	\N	\N	+919752810137	t	Renuka Agrawal	\N	RANPU202512080613056	\N	2025-12-08 06:13:38.748749
\.


--
-- Name: advertisement_clicks_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.advertisement_clicks_id_seq', 1, false);


--
-- Name: advertisements_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.advertisements_id_seq', 5, true);


--
-- Name: chat_attachments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.chat_attachments_id_seq', 1, false);


--
-- Name: chat_messages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.chat_messages_id_seq', 17, true);


--
-- Name: chat_rooms_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.chat_rooms_id_seq', 6, true);


--
-- Name: coupons_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.coupons_id_seq', 1, false);


--
-- Name: district_revenues_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.district_revenues_id_seq', 24, true);


--
-- Name: districts_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.districts_id_seq', 787, true);


--
-- Name: franchise_requests_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.franchise_requests_id_seq', 6, true);


--
-- Name: franchisee_bank_details_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.franchisee_bank_details_id_seq', 1, false);


--
-- Name: franchisee_districts_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.franchisee_districts_id_seq', 2, true);


--
-- Name: franchisee_withdrawal_requests_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.franchisee_withdrawal_requests_id_seq', 1, false);


--
-- Name: message_reports_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.message_reports_id_seq', 1, false);


--
-- Name: monthly_revenue_reports_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.monthly_revenue_reports_id_seq', 1, true);


--
-- Name: otps_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.otps_id_seq', 230, true);


--
-- Name: payment_transactions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.payment_transactions_id_seq', 92, true);


--
-- Name: properties_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.properties_id_seq', 33, true);


--
-- Name: property_inquiries_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.property_inquiries_id_seq', 1, false);


--
-- Name: property_inquiry_status_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.property_inquiry_status_history_id_seq', 1, false);


--
-- Name: property_reels_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.property_reels_id_seq', 7, true);


--
-- Name: property_reviews_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.property_reviews_id_seq', 2, true);


--
-- Name: property_update_requests_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.property_update_requests_id_seq', 1, true);


--
-- Name: property_visits_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.property_visits_id_seq', 4, true);


--
-- Name: reel_interactions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.reel_interactions_id_seq', 19, true);


--
-- Name: review_likes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.review_likes_id_seq', 1, false);


--
-- Name: role_requests_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.role_requests_id_seq', 51, true);


--
-- Name: sub_admin_permission_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.sub_admin_permission_id_seq', 31, true);


--
-- Name: sub_admins_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.sub_admins_id_seq', 1, true);


--
-- Name: subscription_plan_features_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.subscription_plan_features_id_seq', 1, false);


--
-- Name: subscription_plans_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.subscription_plans_id_seq', 6, true);


--
-- Name: subscriptions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.subscriptions_id_seq', 36, true);


--
-- Name: user_following_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.user_following_id_seq', 1, true);


--
-- Name: user_sessions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.user_sessions_id_seq', 174, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: nearprop_user
--

SELECT pg_catalog.setval('public.users_id_seq', 81, true);


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
-- Name: property_visits property_visits_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.property_visits
    ADD CONSTRAINT property_visits_pkey PRIMARY KEY (id);


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
-- Name: sub_admins sub_admins_email_key; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.sub_admins
    ADD CONSTRAINT sub_admins_email_key UNIQUE (email);


--
-- Name: sub_admins sub_admins_pkey; Type: CONSTRAINT; Schema: public; Owner: nearprop_user
--

ALTER TABLE ONLY public.sub_admins
    ADD CONSTRAINT sub_admins_pkey PRIMARY KEY (id);


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
-- Name: TABLE recent_viewed_properties; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.recent_viewed_properties TO nearprop_user;


--
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON TABLES  TO nearprop_user;


--
-- PostgreSQL database dump complete
--

\unrestrict PmAV6LidW13zvMPK0YqulEa4JA3Aafz4y0CBfYx9xZTRfpvXsmqbQu67sxsTIFp


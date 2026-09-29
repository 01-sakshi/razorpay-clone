update merchant set status='ACTIVE', settlement_bank_account='1234567890',
settlement_bank_ifsc='HDFC0001234', settlement_bank_account_holder_name='Sakshi'
where email='sakshi@example.com';

select event,from_status,to_status,payment_id from payment_transition_log order by occurred_at;
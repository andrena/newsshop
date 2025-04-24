// Subscribe.spec.js
import {fireEvent, render, screen} from '@testing-library/vue'
import axios from 'axios';
import Subscribe from '@/subscribe/Subscribe.vue'

jest.mock('axios');

describe('Subscribe.vue', () => {

    function renderSubscribe() {
        render(Subscribe)
        expect(screen.getByText('Newsletter Subscription')).toBeInTheDocument();
    }

    async function subscribeToNewsletter() {
        await fireEvent.update(screen.getByLabelText('E-Mail:'), "test@mail.de");
        await fireEvent.update(screen.getByLabelText('Name:'), "my name");
        await fireEvent.update(screen.getByLabelText('Quelle:'), "Website");
        await fireEvent.click(screen.getByRole('button', {name: 'Abonnieren'}));

        expect(await screen.findByText('Subscription Successful')).toBeInTheDocument();
    }

    test('should be able to add new subscription', async () => {
        axios.post.mockResolvedValueOnce(
            {
                data: {
                    email: 'test@mail.de',
                    name: 'name',
                    source: 'Webseite'
                }
            })

        renderSubscribe();

        await subscribeToNewsletter();
    })

    test('should not be able to use xss on name', async () => {
        axios.post.mockResolvedValueOnce(
            {
                data: {
                    email: 'test@mail.de',
                    name: '<img alt="img-for-xss" src="a" onerror="alert(111)">',
                    source: 'Webseite'
                }
            })

        renderSubscribe();
        await subscribeToNewsletter();

        expect(await screen.findByAltText("img-for-xss")).not.toHaveAttribute('onerror');
    })

    test('should not be able to use xss on source', async () => {
        axios.post.mockResolvedValueOnce(
            {
                data: {
                    email: 'test@mail.de',
                    name: 'Name',
                    source: '<iframe data-testid="frame-xss" src="javascript:console.log(\'xss?\', window.parent.document.body.innerHTML)"></iframe>'
                }
            })


        renderSubscribe()
        await subscribeToNewsletter();
        expect(screen.queryByTestId("frame-xss")).not.toBeInTheDocument();
    })

    test('should not be able to use html for email', async () => {
        axios.post.mockResolvedValueOnce(
            {
                data: {
                    email: '<h1 data-testId="header">My Email Address</h1>',
                    name: 'Name',
                    source: 'source'
                }
            })


        renderSubscribe()
        await subscribeToNewsletter();
        expect(screen.queryByTestId("header")).not.toBeInTheDocument();
        expect(screen.getByText(/My Email Address/)).toBeInTheDocument();
    })

})
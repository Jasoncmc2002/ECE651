import React, { Component } from 'react';
import ContendCard from './contents/ContentCard';
import Brand from '../images/icon-with-name.png';
// import { Bug } from 'react-bootstrap-icons';

class NotFound extends Component {
    state = {}
    render() {
        return (
            <div className="container">
                <div className="row justify-content-md-center align-items-center" style={{ height: "90vh" }}>
                    <div className="col col-md-7">
                        <ContendCard>
                            <div className='d-flex justify-content-center mb-3'>
                                <img src={Brand} className='img-fluid' alt="Brand" style={{ maxWidth: "50%" }}></img>
                            </div>
                            <hr />
                            <h4 className='text-center'>
                                <span className="align-middle">
                                    404: Page does not exist
                                </span>
                            </h4>
                        </ContendCard>
                    </div>
                </div>
            </div>
        );
    }
}

export default NotFound;